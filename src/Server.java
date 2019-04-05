import java.net.SocketException;

public class Server {

    public static void main(String[] args)  {
        ServerReceiver receiver = null;
        try {
            receiver = new ServerReceiver();
            System.out.println("Сервер запущен");
            receiver.work();
        } catch (SocketException e) {
            System.out.println("Порт занят");
        }
    }
}
