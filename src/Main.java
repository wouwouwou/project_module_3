import gui.NameDialog;

/**
 * Start the main program
 * @author Tristan de Boer, Wouter Bos, Gerben Meijer, Tim Hintzbergen
 * @version 1.0
 */
public class Main {
    public static void main (String[] args){
        NameDialog dialog = new NameDialog();
        dialog.pack();
        dialog.setVisible(true);
        //test
    }
}
