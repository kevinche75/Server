import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Users extends Thread{

    private ConcurrentHashMap<Integer, InetAddress> users;
    private int number = 0;
    private static final int MAX_NUMBER = 5;
    private DatagramSocket socket;
    private CollectionPlace collection;

    public Users(DatagramSocket socket, CollectionPlace collection){
        this.socket = socket;
        users = new ConcurrentHashMap<>();
        this.collection = collection;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public synchronized String addUser(InetAddress address, Integer port){
        if(number<MAX_NUMBER){
            users.put(port,address);
            number++;
            System.out.println("===\nДобавлен новый пользователь");
            System.out.println(port);
            System.out.println(address);
            return "CONNECTION";
        } else {
            return "MAX_NUMBER";
        }
    }

    public synchronized void exit(InetAddress address, Integer port){
            if(users.containsKey(port)) {
                users.remove(port);
                number--;
                System.out.println("===\nПользователь вышел");
            }
    }

    @Override
    public void run(){
        users.forEach(this::sendDisconnect);
        System.out.println("===\nВсем разосланы сообщения");
        System.out.println(ServerReaderSaver.save(collection.getCollection()));
    }

    private void sendDisconnect(Integer port, InetAddress address){
        ByteArrayOutputStream bytte = new ByteArrayOutputStream();
        try (ObjectOutputStream sendstream = new ObjectOutputStream(bytte)) {
            sendstream.writeObject(new ServerMessage("DISCONNECTION"));
            sendstream.flush();
            DatagramPacket packet = new DatagramPacket(bytte.toByteArray(), bytte.toByteArray().length, address, port);
            socket.send(packet);
            System.out.println("===\nОтправлено");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
