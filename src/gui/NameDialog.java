package gui;

import gui.controller.MessageController;
import network.NetworkManager;

import javax.swing.*;
import java.awt.event.*;

public class NameDialog extends JDialog {


    // -----<=>-----< Fields >-----<=>----- \\
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JFormattedTextField formattedTextField1;
    private JTextField textField1;


    // -----<=>-----< Constructor(s) >-----<=>----- \\
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
    private void onOK() {
        NetworkManager networkManager = new NetworkManager(textField1.getText());
        MessageController gui = new MessageController(networkManager);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
