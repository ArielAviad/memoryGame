package memoryPlayer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ariel on 26/05/17.
 */
public class ServerListener extends Thread {
    private IController m_controller;

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket client;
    private Properties prop;

    private boolean gameOver;

    public ServerListener(IController controller) {
        m_controller = controller;
    }

    public void startNewGame() {
        startGame();
    }

    private void startGame() {
        try {
            gameOver = false;
            connectToServer();
            getStreams();

            ExecutorService worker = Executors.newFixedThreadPool(1);
            worker.execute(this);
        } catch (EOFException e) {
            displayMessage("\nClient terminated connection");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        displayMessage("waiting for opponent...");
        Properties prop = null;
        try {//get if I start.
            prop = getInput();
            if (prop.getProperty("play").equals("true")) {
                displayMessage("\nopponent connected\nyou start");
            } else {
                displayMessage("\nopponent connected\nopponent turn");
            }
            m_controller.setMyTurn(Boolean.parseBoolean(prop.getProperty("play")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (!gameOver) {
                try {
                    prop = getInput();
                    processMessage(prop);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            displayMessage("game over...");
            m_controller.displayScore(
                    Integer.parseInt(prop.getProperty("your score")),
                    Integer.parseInt(prop.getProperty("opponent score"))
                    );
            closeConnection();
        }
    }

    private void processMessage(Properties prop) {
        //case respond to my move.
        gameOver = prop.getProperty("game over").equals("true");
        if (prop.getProperty("move").equals("you")) {
            if (prop.getProperty("legal move").equals("legal")) {
                executeMove(prop, false);
                displayMessage("\nopponent turn.");
            } else {
                displayMessage("\nnot a legal move.\nyour move");
                m_controller.setMyTurn(true);
            }
        } else {//opponent move
            executeMove(prop, gameOver ? false : true);
            if (!gameOver)
                displayMessage("\nyour turn");
            else {
                try {
                    Properties properties = new Properties();
                    properties.setProperty("game over","true");
                    output.writeObject(properties);
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void executeMove(Properties prop, boolean isMyTurn) {
        showTurn(prop);
        m_controller.setMyTurn(isMyTurn);
    }

    private void showTurn(Properties prop) {
        m_controller.exposedCard(
                Integer.parseInt(prop.getProperty("first card")),
                Integer.parseInt(prop.getProperty("first id")),
                Boolean.parseBoolean(prop.getProperty("expose"))
        );
        m_controller.exposedCard(
                Integer.parseInt(prop.getProperty("second card")),
                Integer.parseInt(prop.getProperty("second id")),
                Boolean.parseBoolean(prop.getProperty("expose"))
        );
        gameOver = Boolean.parseBoolean(prop.getProperty("game over"));
        if (gameOver) {
            displayMessage("\ngame ended...");
        }
    }


    private Properties getInput() throws ClassNotFoundException, IOException {
        return (Properties) input.readObject();
    }

    private void connectToServer() throws IOException {
        displayMessage("\nAttempting to connection");
        client = new Socket(InetAddress.getByName(m_controller.getHost()), m_controller.getServerPort());
        displayMessage("\nconnected to: " + client.getInetAddress().getHostName());
        displayMessage("\nwaiting for openent...");
    }

    private void getStreams() throws IOException {
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush();
        input = new ObjectInputStream(client.getInputStream());
        displayMessage("\nGot I/O streams\n");
    }

    private void displayMessage(String s) {
        m_controller.logWrite(s);
    }

    private void closeConnection() {
        try {
            input.close();
            output.close();
            client.close();
            displayMessage("\nsuccess disconnecting...");
        } catch (IOException e) {
            displayMessage("\nfail disconnecting...");
        }
    }

    public void makeMove() {
        try {
            sendToServer();
        } catch (IOException e) {
            displayMessage("\nfail send move");
        }
    }

    private void sendToServer() throws IOException {
        if (m_controller.isMyTurn()) {
            m_controller.setMyTurn(false);
            Integer[] selected = m_controller.getSelected();
            final Properties prop = new Properties();
            prop.setProperty("first card", selected[0].toString());
            prop.setProperty("second card", selected[1].toString());
            output.writeObject(prop);
            output.flush();
            m_controller.clearSelected();
        }
    }

}
