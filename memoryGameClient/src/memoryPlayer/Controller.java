package memoryPlayer;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by ariel on 26/05/17.
 */
public class Controller implements IController,Observer {
    private IModel m_model;
    private IView m_view;
    private ServerListener listener;


    public Controller(IModel model) {
        m_model = model;
        m_view = new View(this,model);
        m_view.createView();
        m_view.createControls();
        model.initialize();
        listener = new ServerListener(this);
        model.registerObserver(this);
    }

    @Override
    public void startNewGame() {
        listener.startNewGame();
        m_view.restart();
    }

    @Override
    public boolean cardSelected(int i) {
        return m_model.setSelected(i);
    }

    @Override
    public String getHost() {
        return m_model.getHost();
    }

    @Override
    public int getServerPort() {
        return m_model.getServerPort();
    }

    @Override
    public void setMyTurn(boolean turn) {
        m_model.setTurn(turn);
    }

    @Override
    public boolean isMyTurn() {
        return m_model.isMyTurn();
    }

    @Override
    public void exposedCard(int pos, int cardID, boolean exposeAlways) {
        m_view.exposeCard(pos,cardID,exposeAlways);
    }

    @Override
    public void logWrite(String s) {
        m_view.logWrite(s);
    }

    @Override
    public void clearSelected() {
        m_model.clearSelected();
    }

    @Override
    public void unSelect(int i) {
        m_model.unSelect(i);
    }

    @Override
    public Integer[] getSelected() {
        return m_model.getSelected();
    }

    @Override
    public void displayScore(int myScore, int opponentScore) {
        if (myScore == opponentScore)
            m_view.displayScore("Tie " + myScore + " : " + opponentScore);
        else if (myScore < opponentScore)
            m_view.displayScore("You lost " + myScore + " : " + opponentScore);
        else
            m_view.displayScore("You won " + myScore + " : " + opponentScore);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (m_model.isMyTurn() && m_model.getSelected().length == 2){
            listener.makeMove();
        }
    }
}
