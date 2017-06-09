/**
 * Created by ariel on 26/05/17.
 */
public class Model implements IModel {
    IBoard board;
    int[] playersScore;
    boolean isGameOver;

    @Override
    public void initialize(int boardSize) {
        board = new Board(boardSize);
        playersScore = new int[2];
        isGameOver = false;
    }

    @Override
    public void createNewBoard() {
        board = new Board(board.getSize());
        playersScore[0] = 0;
        playersScore[1] = 0;
        isGameOver = false;
    }

    @Override
    public Move setMove(int player, int loc1, int loc2) {
        Move move = null;
        if (isLegalMove(loc1, loc2) && between(player,0,2)){
            ICard first = board.getCard(loc1),second = board.getCard(loc2);
            move = new Move(first,second,first.getValue() == second.getValue());
            if (first.getValue() == second.getValue()){
                board.flip(loc1);
                board.flip(loc2);
                playersScore[player] += 1;
                isGameOver = board.isFull();
            }
        }
        return move;
    }

    private boolean isLegalMove(int loc1, int loc2) {
        return loc1 != loc2 && between(loc1,0,board.getSize()) &&
                between(loc2,0,board.getSize()) &&
                !board.isFlip(loc1) && !board.isFlip(loc2);
    }


    @Override
    public boolean isGameOver() {
        return board.isFull();
    }

    @Override
    public int getScore(int playerNumber) {
        return playersScore[playerNumber];
    }

    boolean between(int n, int from, int to){
        return n >= from && n < to;
    }
}
