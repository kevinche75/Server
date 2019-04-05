import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Executor extends Thread {

    private DatagramPacket packet;
    private Users users;
    private CollectionPlace collection;
    private byte[] buffer;
    private DatagramSocket socket;

public Executor(DatagramPacket packet, CollectionPlace collection, Users users, byte[] buffer){
    this.packet = packet;
    this.collection = collection;
    this.users = users;
    this.buffer = buffer;
    socket = users.getSocket();
    }

    @Override
    public void run() {
        try (ObjectInputStream receivedstream = new ObjectInputStream(new ByteArrayInputStream(buffer))) {
            analyze((ClientMessage) receivedstream.readObject());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void analyze(ClientMessage message){
        System.out.println("Сообщение прочитано");
        System.out.println(message.getMessage());
        switch (message.getMessage()){
            case "reorder":
                sendMessage(new ServerMessage(collection.reorder()));
                break;
            case "info":
                sendMessage(new ServerMessage(collection.info()));
                break;
            case "add":
                sendMessage(new ServerMessage(collection.add((Alice)message.getArgument())));
                break;
            case "import":
                sendMessage(new ServerMessage(collection.importCollection((CopyOnWriteArrayList<Alice>) message.getArgument())));
                break;
            case "load":
                sendMessage(new ServerMessage(collection.load()));
                break;
            case "show":
                sendMessage(new ServerMessage(collection.show()));
                break;
            case "remove_greater":
                sendMessage(new ServerMessage(collection.remove_greater((Alice) message.getArgument())));
                break;
            case "remove_all":
                sendMessage(new ServerMessage(collection.remove_all((Alice)message.getArgument())));
                break;
            case "remove":
                sendMessage(new ServerMessage(collection.remove((Alice)message.getArgument())));
                break;
            case "connect":
                sendMessage(new ServerMessage(users.addUser(packet.getAddress(), packet.getPort())));
                break;
            case "exit":
                users.exit(packet.getAddress(), packet.getPort());
                break;
            case "save":
                sendMessage(new ServerMessage(collection.save()));
                break;
            default:
                sendMessage(new ServerMessage("Неизвестная команда"));
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
}
