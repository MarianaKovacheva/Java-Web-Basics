package javache;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.FutureTask;

public class Server {

    private static final String LISTENING_MESSAGE = "Listening on port: ";

    private static final String TIMEOUT_DETECTION_MESSAGE = "Timeout detected!";

    private static final Integer SOCKET_TIMEOUT_MILLISECONDS = 5000;

    private int port;

    private int timeouts;

    private ServerSocket server;

    public Server(int port){
        this.port = port;
        this.timeouts = 0;
    }

    //The method run() is with the idea to start the socket connection.
    public void run() throws IOException{
        this.server = new ServerSocket(this.port);
        System.out.println(LISTENING_MESSAGE + this.port);

        //We give a timeout to the server, if we don't get either the request, or the response,
        //for the client not to be all the time on the server, because it can give problems if many
        //requests are not processed by the server and the possibility of server crashing.
        this.server.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);

        //The while cycle is for the server waiting to be "eternal" and to wait and check for new connection.
        while(true){
            try(Socket clientSocket = this.server.accept()){

                clientSocket.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);

                ConnectionHandler connectionHandler =
                        new ConnectionHandler(clientSocket, new RequestHandler());

                //The asynchronous of the server is created by this Future task,
                // which is waiting to be done in the future.
                FutureTask<?> task = new FutureTask<>(connectionHandler,null);
                task.run();
            }catch(SocketTimeoutException e){
                System.out.println(TIMEOUT_DETECTION_MESSAGE);
                this.timeouts++;
            }
        }
    }
}
