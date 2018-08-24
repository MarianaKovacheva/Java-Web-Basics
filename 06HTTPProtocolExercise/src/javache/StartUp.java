package javache;

import java.io.IOException;

public class StartUp {
    public static void main(String[] args) {
        start(args);
    }

    private static void start(String[] args) {
        //In this way we use the default server port, which is 8000.
        int port = WebConstants.DEFAULT_SERVER_PORT;

        if(args.length > 1){
            port = Integer.parseInt(args[1]);
        }

        //Creates an object of type server, which is a class and receives the port, which should be 8000.
        Server server = new Server(port);

        try{
            server.run();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
