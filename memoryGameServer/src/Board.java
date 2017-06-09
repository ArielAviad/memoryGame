import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

/**
 * Created by ariel on 27/05/17.
 */
public class Board implements IBoard{
    Card[] board;
    int nFlipped;

    public Board(int size) {
        double n = Math.sqrt((double)size);
        if (n != (int)n)
            throw new IllegalArgumentException("size not equal to n^2");
        if (n%2 != 0)
            throw new IllegalArgumentException("size not even");
        board = new Card[size];
        initBoard();
    }

    private void initBoard() {
        IntStream.range(0,board.length/2).forEach(i->
            IntStream.range(0,2).forEach(j->
                    board[i*2+j] = new Card(i)
        ));
        shuffle();
        nFlipped = 0;
    }

    @Override
    public Card getCard(int i) {
        return board[i];
    }

    @Override
    public void flip(int i) {
        nFlipped += isFlip(i) ? -1 : 1;
        board[i].flip();
    }

    @Override
    public boolean isFlip(int i) {
        return board[i].isFlip();
    }

    @Override
    public int getSize() {
        return board.length;
    }

    @Override
    public void shuffle() {
        Collections.shuffle(Arrays.asList(board));
    }

    @Override
    public boolean isFull() {
        return nFlipped == board.length;
    }
}
