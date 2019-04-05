import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerReceiver {

    private final static int port = 1488;
    private DatagramSocket socket;
    private CollectionPlace collection;
    private  byte[] buffer;
    private Users users;

    public ServerReceiver() throws SocketException{
        socket = new DatagramSocket(port);
        try {
            collection = new CollectionPlace(ServerReaderSaver.justReadFile());
        } catch (FileNotFoundException e) {
            collection = new CollectionPlace(new CopyOnWriteArrayList<>());
        }
        users = new Users(socket, collection);
    }

    private void shootDown(){
        Runtime.getRuntime().addShutdownHook(users);
    }

    public void work() {
        shootDown();
        while (true){
            buffer = new byte[8192];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(incoming);
                System.out.println("Получено сообщение");
                new Executor(incoming, collection, users, buffer).start();
            } catch (IOException e) {
                System.out.println("===\nОшбика получения пакета");
            }
        }
    }
}
