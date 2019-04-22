
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.CopyOnWriteArrayList;
import com.lambdaworks.codec.Base64;
import com.lambdaworks.crypto.SCrypt;

public class Executor extends Thread {

    private DatagramPacket packet;
    private SQLWorker collection;
    private byte[] buffer;
    private DatagramSocket socket;
    private TokenFactory tokenFactory;
    private RegistrationTokenFactory registrationTokenFactory;
    private SenderMails senderMails;

public Executor(SenderMails senderMails, DatagramPacket packet, SQLWorker collection, byte[] buffer, TokenFactory tokenFactory, RegistrationTokenFactory registrationTokenFactory){
    this.packet = packet;
    this.collection = collection;
    this.buffer = buffer;
    socket = tokenFactory.getSocket();
    this.tokenFactory = tokenFactory;
    this.registrationTokenFactory = registrationTokenFactory;
    this.senderMails = senderMails;
    }

    @Override
    public void run() {
        try (ObjectInputStream receivedstream = new ObjectInputStream(new ByteArrayInputStream(buffer))) {
            analyze((ClientMessage) receivedstream.readObject());
        } catch (ClassNotFoundException e) {
            sendMessage(new ServerMessage("===\nОшибка получения данных, сообщение не корректно", "ERROR"));
        } catch (IOException e) {
            sendMessage(new ServerMessage("===\nОшибка получения данных, обратитесь к знающему человеку, IOException, тикайте с компьютера", "ERROR"));
        }
    }


    private void analyze(ClientMessage message){
        System.out.println("Сообщение прочитано");
        System.out.println(message.getMessage());
        String login;
        switch (message.getMessage()){
            case "info":
                login = tokenFactory.checkToken(message.getToken());
                if(login == null){
                    sendMessage(new ServerMessage("===\nВы не подключены к серверу", "DISCONNECTION"));
                    return;
                }
                sendMessage(new ServerMessage(collection.info(), "COMMAND_RESULT"));
                break;
            case "add":
                login = tokenFactory.checkToken(message.getToken());
                if(login == null){
                    sendMessage(new ServerMessage("===\nВы не подключены к серверу", "DISCONNECTION"));
                    return;
                }
                sendMessage(new ServerMessage(collection.add((Alice)message.getArgument(),login), "COMMAND_RESULT"));
                break;
            case "import":
                login = tokenFactory.checkToken(message.getToken());
                if(login == null){
                    sendMessage(new ServerMessage("===\nВы не подключены к серверу", "DISCONNECTION"));
                    return;
                }
                sendMessage(new ServerMessage(collection.importCollection((CopyOnWriteArrayList<Alice>) message.getArgument(),login), "COMMAND_RESULT"));
                break;
            case "show":
                login = tokenFactory.checkToken(message.getToken());
                if(login == null){
                    sendMessage(new ServerMessage("===\nВы не подключены к серверу", "DISCONNECTION"));
                    return;
                }
                sendMessage(new ServerMessage(collection.show(), "COMMAND_RESULT"));
                break;
            case "remove_greater":
                login = tokenFactory.checkToken(message.getToken());
                if(login == null){
                    sendMessage(new ServerMessage("===\nВы не подключены к серверу", "DISCONNECTION"));
                    return;
                }
                sendMessage(new ServerMessage(collection.remove_greater((Alice) message.getArgument(), login), "COMMAND_RESULT"));
                break;
            case "remove_all":
                login = tokenFactory.checkToken(message.getToken());
                if(login == null){
                    sendMessage(new ServerMessage("===\nВы не подключены к серверу", "DISCONNECTION"));
                    return;
                }
                sendMessage(new ServerMessage(collection.remove_all((Alice)message.getArgument(),login), "COMMAND_RESULT"));
                break;
            case "remove":
                login = tokenFactory.checkToken(message.getToken());
                if(login == null){
                    sendMessage(new ServerMessage("===\nВы не подключены к серверу", "DISCONNECTION"));
                    return;
                }
                sendMessage(new ServerMessage(collection.remove((Alice)message.getArgument(), login), "COMMAND_RESULT"));
                break;
            case "show_yours":
                login = tokenFactory.checkToken(message.getToken());
                if(login == null){
                    sendMessage(new ServerMessage("===\nВы не подключены к серверу", "DISCONNECTION"));
                    return;
                }
                sendMessage(new ServerMessage(collection.show_yours(login), "COMMAND_RESULT"));
                break;
            case "connect":
                sendMessage(new ServerMessage("===\nВы подключились к серверу", "CONNECTION"));
                return;
            case "exit":
                login = tokenFactory.checkToken(message.getToken());
                if(login == null){
                    sendMessage(new ServerMessage("===\nВы не подключены к серверу", "DISCONNECTION"));
                    return;
                }
                tokenFactory.exit(message.getToken());
                break;
            case "login":
                Pair<String, String> pair = (Pair<String, String>) message.getArgument();
                if(collection.checkLoginAndPassword(pair.getKey(), getHash(pair.getValue()))){
                    int token = tokenFactory.addToken(pair.getKey(), packet.getAddress(), packet.getPort());
                    if(token<0){
                        sendMessage(new ServerMessage("===\nМаксимальное количество пользователей на сервере", "MAX_NUMBER"));
                        return;
                    }
                    sendMessage(new ServerMessage(Integer.toString(token), "TRUE_LOGIN"));
                    return;
                }
                sendMessage(new ServerMessage("===\nНеправильный логин или пароль", "FALSE_LOGIN"));
                break;
            case "register":
                Pair<String, String> registration = (Pair<String, String>) message.getArgument();
                if(!collection.checkRegistration(registration.getKey())){
                    sendMessage(new ServerMessage("===\nТакой пользователь уже существует", "FALSE_REGISTER_LOGIN"));
                    return;
                }
                int token = registrationTokenFactory.addToken(registration.getKey(), getHash(registration.getValue()));
                if(token>0){
                    if(senderMails.sendMessage(registration.getKey(), token)==null){
                        sendMessage(new ServerMessage("===\nНеправильный формат логина", "FALSE_REGISTER_LOGIN"));
                        return;
                    } else {
                        sendMessage(new ServerMessage("===\nВам на почту отправлен уникальный код", "TRUE_REGISTER_LOGIN"));
                        return;
                    }
                } else {
                    sendMessage(new ServerMessage("===\nТакой пользователь уже существует, либо токен для этого пользователя ещё активен", "FALSE_REGISTER_LOGIN"));
                    return;
                }
            case "token":
                if(registrationTokenFactory.checkLogin((String)message.getArgument())) {
                    Pair<String, String> forlogin = registrationTokenFactory.checkToken(message.getToken(), (String)message.getArgument());
                    if (forlogin == null) {
                        sendMessage(new ServerMessage("===\nНеправильно указан уникальный код", "FALSE_REGISTRATION"));
                        return;
                    } else {
                        collection.createUser(forlogin.getKey(), getHash(forlogin.getValue()));
                        sendMessage(new ServerMessage("===\nВы успешно зарегистрированы", "TRUE_REGISTRATION"));
                        return;
                    }
                } else sendMessage(new ServerMessage("===\nВремя существования уникального кода истекло", "FALSE_REGISTRATION"));
                return;
            default:
                sendMessage(new ServerMessage("===\nНеизвестная команда", "ERROR"));
                break;
        }
    }

    private void sendMessage(ServerMessage message){
        ByteArrayOutputStream bytte = new ByteArrayOutputStream();
        try (ObjectOutputStream sendstream = new ObjectOutputStream(bytte)) {
            sendstream.writeObject(message);
            sendstream.flush();
            DatagramPacket serverpacket = new DatagramPacket(bytte.toByteArray(), bytte.toByteArray().length, packet.getAddress(), packet.getPort());
            socket.send(serverpacket);
            System.out.println("Отправлено");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getHash(String pass) {
        try {
            byte[] derived = SCrypt.scrypt(pass.getBytes(), "salt".getBytes(), 16, 16, 16, 32);
            return new String(Base64.encode(derived));
        } catch (Exception e) {
            return null;
        }
    }
}
