package memoryPlayer;

/**
 * Created by ariel on 26/05/17.
 */
public interface IView {
    void createView();

    void createControls();

    void exposeCard(int pos, int cardID, boolean exposeAlways);

    void logWrite(String s);

    void displayScore(String s);

    void restart();
}
