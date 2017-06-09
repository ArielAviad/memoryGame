/**
 * Created by ariel on 26/05/17.
 */
public interface IModel {
    void initialize(int boardSize);

    void createNewBoard();

    Move setMove(int player,int loc1, int loc2);

    //String getLocId(int loc);

    boolean isGameOver();

    int getScore(int playerNumber);
}
