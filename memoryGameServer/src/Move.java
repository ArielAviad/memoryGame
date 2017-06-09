/**
 * Created by ariel on 27/05/17.
 */
public class Move {
    private ICard first,second;
    private boolean toRevel;
    public Move(ICard first,ICard second,boolean toRevel){
        this.first = first;
        this.second = second;
        this.toRevel = toRevel;
    }

    public int getFirstId() {
        return first.getValue();
    }

    public int getSecond() {
        return second.getValue();
    }

    public boolean isToRevel() {
        return toRevel;
    }
}
