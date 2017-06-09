package memoryPlayer;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.IntStream;

public class View extends JFrame implements IView {
    private IModel m_model;
    private IController m_controller;

    private JTextArea displayArea;
    private BoardPanel boardPanel;

    public View(Controller controller, IModel model) {
        super("Memory game");
        m_model = model;
        m_controller = controller;
    }

    @Override
    public void createView() {
        setMenu();
        displayArea = new JTextArea(4,30);
        displayArea.setEditable(false);
        displayArea.setAutoscrolls(true);
        DefaultCaret caret = (DefaultCaret)displayArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane,BorderLayout.SOUTH);

        //TODO to dicde when to create this.
        boardPanel = new BoardPanel(16);
        add(boardPanel,BorderLayout.CENTER);
        boardPanel.repaint();

        //pack();
        setSize(400,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGame();
            }
        });
        setVisible(true);
    }


    private void setMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu options = new JMenu("options");
        options.setMnemonic('O');

        startNewGame = new JMenuItem("new game");
        startNewGame.setMnemonic('N');


        exit = new JMenuItem("exit");
        exit.setMnemonic('Q');

        options.add(startNewGame);
        options.addSeparator();
        options.add(exit);

        menuBar.add(options);
        setJMenuBar(menuBar);
    }

    JMenuItem startNewGame;
    JMenuItem endGame;
    JMenuItem exit;

    @Override
    public void createControls() {
        startNewGame.addActionListener(e-> m_controller.startNewGame());
        exit.addActionListener(e->exitGame());
    }

    @Override
    public void exposeCard(int pos, int cardID, boolean exposeAlways) {
        boardPanel.exposeCard(pos,cardID,exposeAlways);
    }

    @Override
    public void logWrite(String s) {
        displayArea.append(s);
    }

    @Override
    public void displayScore(String msg) {
        JOptionPane.showMessageDialog(this,msg,"Score",JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void restart() {
        boardPanel.resart();
    }

    private void exitGame() {
        System.exit(0);
    }

    private class BoardPanel extends JPanel implements Observer{
        private GridLayout gridLayout;

        private Card[] cards;

        public BoardPanel(int nCards){
            m_model.registerObserver(this);
            int lineLen = (int)(Math.sqrt(nCards));
            gridLayout = new GridLayout(lineLen,lineLen,5,5);
            setLayout(gridLayout);

            cards = new Card[nCards];

            initBoard(nCards);
        }

        private void initBoard(int nCards) {
            IntStream.range(0,nCards).forEach(i -> {
                cards[i] = new Card(""+(i+1),i);
                initCard(i);
                add((Component) cards[i]);
            });
        }

        private void initCard(int i) {
            cards[i].setFlip(false);
            cards[i].setActionListener(
                    e->cardPressed((Card)e.getSource())
            );
        }

        private void cardPressed(Card card) {
            if (m_controller.cardSelected(Integer.parseInt(card.getText())-1)){
                card.setSelected(true);
            }else if (card.isSelected() && !card.isFlip()){
                card.setSelected(false);
                m_controller.unSelect(Integer.parseInt(card.getText())-1);
            }
        }

        @Override
        public void update(Observable o, Object arg) {
            Integer[] selected = m_model.getSelected();
            if (selected.length == 0)
                Arrays.stream(cards).forEach(card -> {
                    if (!card.isFlip())
                        card.setSelected(false);
                });
        }

        public void exposeCard(int pos, int cardID, boolean exposeAlways) {
            cards[pos].setActionListener((e)-> {});
            if (exposeAlways == true) {
                flipCard(cards[pos], cardID);
            }
            else {
                new Thread(()->{
                    flipCard(cards[pos], cardID);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }cards[pos].setText("" + (pos+1));
                    cards[pos].setActionListener(e->cardPressed((Card)e.getSource()));
                    cards[pos].setFlip(false);
                }).start();
            }
        }

        private void flipCard(Card card, int cardID) {
            card.setFlip(true);
            card.setText(""+cardID);
        }

        public void resart() {
            IntStream.range(0,cards.length).forEach(i->initCard(i));
        }
    }
}