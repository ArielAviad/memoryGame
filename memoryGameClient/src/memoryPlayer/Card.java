package memoryPlayer;

import javax.swing.*;
import java.awt.event.ActionListener;


public class Card extends JToggleButton{
    private ActionListener action = null;
    private int pos;
    private boolean isFlip;

    public Card(String title,int position){
        super(title);
        isFlip = false;
        pos = position;
        super.addActionListener((e)->{
            if (action != null)
                action.actionPerformed(e);
        });
    }

    public void setActionListener(ActionListener l) {
        action = l;
    }

    public int getPos() {
        return pos;
    }

    @Override
    public void addActionListener(ActionListener l) {}

    public boolean isFlip() {
        return isFlip;
    }

    public void setFlip(boolean flip) {
        this.isFlip = flip;
        super.setSelected(flip);
        if (isFlip == false){
            setText(""+(pos+1));
        }
    }

    @Override
    public void setSelected(boolean b) {
        if (!isFlip)
            super.setSelected(b);
    }
}
