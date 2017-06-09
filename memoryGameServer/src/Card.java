/**
 * Created by ariel on 27/05/17.
 */
public class Card implements ICard {
    private int id;
    private boolean isFlip;

    public Card(int i) {
        id = i;
        isFlip = false;
    }

    @Override
    public void flip() {isFlip = !isFlip;}

    @Override
    public boolean isFlip() {
        return isFlip;
    }

    @Override
    public int getValue() {
        return id;
    }

}
