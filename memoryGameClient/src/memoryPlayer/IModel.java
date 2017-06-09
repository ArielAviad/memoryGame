package memoryPlayer;

import java.util.Observer;

/**
 * Created by ariel on 26/05/17.
 */
public interface IModel {

    void initialize();

    String getHost();

    int getServerPort();

    void registerObserver(Observer o);

    void removeObserver(Observer o);

    Integer[] getSelected();

    boolean setSelected(int i);

    void setTurn(boolean myTurn);

    boolean isMyTurn();

    void clearSelected();

    void unSelect(int i);
}
