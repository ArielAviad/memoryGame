package memoryPlayer;

/**
 * Created by ariel on 26/05/17.
 */
public interface IController {

    void startNewGame();

    boolean cardSelected(int i);

    String getHost();

    int getServerPort();

    void setMyTurn(boolean turn);

    boolean isMyTurn();

    void exposedCard(int pos, int cardID, boolean exposeAlways);

    void logWrite(String s);

    void clearSelected();

    void unSelect(int i);

    Integer[] getSelected();

    void displayScore(int myScore,int opponentScore);

}
