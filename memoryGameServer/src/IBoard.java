/**
 * Created by ariel on 27/05/17.
 */
public interface IBoard {
    Card getCard(int i);
    
    void flip(int i);
    
    boolean isFlip(int i);
    
    int getSize();
    
    void shuffle();

    boolean isFull();
}
