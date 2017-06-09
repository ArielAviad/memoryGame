import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Controller implements IController {


    @Override
    public void startServer() {
        ServerSocket serverSocket = null;
        try {
            log("start lenten to port 3000");
            serverSocket = new ServerSocket(3000);
        } catch (IOException e) {
            logErr("unable to start listen to port 3000");
            System.exit(1);
        }
        Socket socketPlayer1;
        Socket socketPlayer2;
        while (true) {
            try {
                IModel model = new Model();
                Game game = new Game(model);
                log("waiting for first player...");
                socketPlayer1 = serverSocket.accept();
                game.makePlayer(0,socketPlayer1);
                log("waiting for second player");
                socketPlayer2 = serverSocket.accept();
                game.makePlayer(1,socketPlayer2);
                game.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void log(String msg){
        System.out.println(msg);
    }

    public void logErr(String err){
        System.err.println(err);
    }
}
