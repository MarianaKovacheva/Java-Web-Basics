import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        Server server = new Server(8001);

        try{
            server.run();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
