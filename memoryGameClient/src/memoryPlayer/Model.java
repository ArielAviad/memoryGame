package memoryPlayer;

import java.util.*;

/**
 * Created by ariel on 26/05/17.
 */
public class Model extends Observable implements IModel {
    private boolean myTurn;
    private String host;
    private int serverPort;
    private List<Integer> selected;

    public Model() {
        this("localhost",3000);
    }

    public Model(String host, int port) {
        this.host = host;
        serverPort = port;
        selected = new ArrayList<>();
    }

    @Override
    public void initialize() {
        myTurn = true;
    }

    @Override
    public String getHost() {return host;}

    @Override
    public int getServerPort(){return serverPort;}

    @Override
    public void registerObserver(Observer o) {
        addObserver(o);
    }

    @Override
    public void removeObserver(Observer o) {
        deleteObserver(o);
    }

    @Override
    public Integer[] getSelected() {
        return selected.toArray(new Integer[selected.size()]);
    }

    @Override
    public boolean setSelected(int i) {
        boolean isSelect = false;
        if (selected.size()<2 && !selected.contains(i) && myTurn) {
            isSelect = selected.add(i);
            stateChange();
        }
        return isSelect;
    }

    @Override
    public void setTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    @Override
    public boolean isMyTurn(){return myTurn;}

    @Override
    public void clearSelected() {
        selected.clear();
        ;
        stateChange();
    }

    @Override
    public void unSelect(int i) {
        int index = selected.indexOf(i);
        if (index>-1)
            selected.remove(index);
    }


    private void stateChange() {
        setChanged();
        notifyObservers();
    }
}
