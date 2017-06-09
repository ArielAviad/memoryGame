
import memoryPlayer.Controller;
import memoryPlayer.IController;
import memoryPlayer.IModel;
import memoryPlayer.Model;

import javax.swing.*;

/**
 * Created by ariel on 26/05/17.
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(()->{
            IModel model;
            if (args.length > 1)
                model = new Model(args[0],Integer.parseInt(args[1]));
            else
                model = new Model();
            IController controller = new Controller(model);
        });
    }
}
