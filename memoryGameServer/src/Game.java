import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Game {
    private IModel m_model;
    private Move lastMove;
    private Player[] players;
    private final static int PLAYER_1 = 0;
    private final static int PLAYER_2 = PLAYER_1 + 1;
    private volatile int currentPlayer;
    private Lock gameLock;
    private Condition otherPlayerConnected;
    private Condition otherPlayerTurn;
    private ExecutorService runGame;
    private boolean isGameStarted = false;

    public Game(IModel model) {
        m_model = model;

        runGame = Executors.newFixedThreadPool(2);
        gameLock = new ReentrantLock();

        otherPlayerConnected = gameLock.newCondition();
        otherPlayerTurn = gameLock.newCondition();

        model.initialize(16);
        model.createNewBoard();

        players = new Player[2];

    }

    public void execute(){
        gameLock.lock();

        SecureRandom sr = new SecureRandom();
        currentPlayer = sr.nextInt(players.length);

        try {
            players[currentPlayer].setSuspended(false);
            IntStream.range(0,players.length).forEach(
                    nPlayer->runGame.execute(players[nPlayer])
            );
//            otherPlayerConnected.signal();
        }finally {
            gameLock.unlock();
        }
    }


    public void makePlayer(int nPlayer,Socket socket){
        players[nPlayer] = new Player(this,socket,nPlayer);
    }

    public boolean validateAndMove(int loc1, int loc2,int player) {
        while (player != currentPlayer){
            gameLock.lock();
            try {
                otherPlayerTurn.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                gameLock.unlock();
            }

        }

        lastMove = m_model.setMove(currentPlayer,loc1, loc2);
        if (lastMove != null) {
            currentPlayer = (currentPlayer + 1) % players.length;
            players[currentPlayer].otherPlayerMove(loc1, loc2);

            gameLock.lock();

            try {
                otherPlayerTurn.signal();
            } finally {
                gameLock.unlock();
            }
            return true;
        } else
            return false;
    }

    public class Player extends Thread {
        Game m_game;

        private Socket connection;
        private ObjectOutputStream output;
        private ObjectInputStream input;
        private int playerNumber;
        private boolean suspended = true;

        public Player(Game game, Socket socket, int number) {
            m_game = game;
            playerNumber = number;
            connection = socket;
            try {
                output = new ObjectOutputStream(connection.getOutputStream());
                output.flush();
                input = new ObjectInputStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void otherPlayerMove(int loc1,int loc2) {
            Properties prop = new Properties();
            prop.setProperty("move", "opponent");
            prop.setProperty("first card", "" + loc1);
            prop.setProperty("first id", "" + lastMove.getFirstId());
            prop.setProperty("second card", "" + loc2);
            prop.setProperty("second id", "" + lastMove.getSecond());
            prop.setProperty("expose", ""+lastMove.isToRevel());
            prop.setProperty("game over", ""+m_model.isGameOver());
            prop.setProperty("your score",""+m_model.getScore(currentPlayer));
            prop.setProperty("opponent score",""+m_model.getScore((currentPlayer+1)%2));
            try {
                sendData(prop);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendData(Properties prop) throws IOException {
            output.writeObject(prop);
            output.flush();
        }

        @Override
        public void run() {
            Properties prop = new Properties();
            try {
                prop.clear();
                prop.setProperty("msg", "opponent connect");
                prop.setProperty("game start","true");
                if (playerNumber == currentPlayer) {
                    prop.setProperty("play","true");
                }
                else
                    prop.setProperty("play","false");
                try {
                    output.writeObject(prop);
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while ((!m_model.isGameOver())) {
                    int loc1 = 0, loc2 = 0;
                    try {
                        prop = (Properties) input.readObject();
                    } catch (IOException e) {
                        System.err.println("player:"+currentPlayer);
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (!prop.containsKey("game over") || !prop.getProperty("game over").equals("true")) {
                        loc1 = Integer.parseInt(prop.getProperty("first card"));
                        loc2 = Integer.parseInt(prop.getProperty("second card"));
                        if (validateAndMove(loc1, loc2, playerNumber)) {
                            prop.clear();
                            prop.setProperty("move", "you");
                            prop.setProperty("legal move", "legal");
                            prop.setProperty("first card", "" + loc1);
                            prop.setProperty("first id", "" + lastMove.getFirstId());
                            prop.setProperty("second card", "" + loc2);
                            prop.setProperty("second id", "" + lastMove.getSecond());
                            prop.setProperty("expose", "" + lastMove.isToRevel());
                            prop.setProperty("game over", "" + m_model.isGameOver());
                            prop.setProperty("your score", "" + m_model.getScore((currentPlayer + 1) % 2));
                            prop.setProperty("opponent score", "" + m_model.getScore(currentPlayer));
                            try {
                                sendData(prop);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            prop.clear();
                            prop.setProperty("move", "you");
                            prop.setProperty("legal move", "not legal");
                            try {
                                sendData(prop);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } finally {
                try {

                    input.close();
                    output.close();
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void setSuspended(boolean status) {
            this.suspended = status;
        }
    }

}
