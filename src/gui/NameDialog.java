package gui;

import gui.controller.MessageController;
import network.NetworkManager;

import javax.swing.*;
import java.awt.event.*;

/**
 * Class for getting the name of the User.
 * @author Tristan de Boer
 * @since 10-4-15
 */
public class NameDialog extends JDialog {


    // -----<=>-----< Fields >-----<=>----- \\
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;


    // -----<=>-----< Constructor(s) >-----<=>----- \\
    /**
     * Constructor for a NameDialog.
     */
    public NameDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    // -----<=>-----< Methods >-----<=>----- \\
    /**
     * ActionHandler for when the OK button has been pushed.
     */
    private void onOK() {
        NetworkManager networkManager = new NetworkManager(textField1.getText());
        MessageController gui = new MessageController(networkManager);
        dispose();
    }

    /**
     * ActionHandler for when the Cancel button has been pushed.
     */
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
