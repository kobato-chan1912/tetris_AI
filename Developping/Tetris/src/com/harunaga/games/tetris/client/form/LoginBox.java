package com.harunaga.games.tetris.client.form;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.harunaga.games.common.client.font.Font;
import com.harunaga.games.common.client.input.HTextField;
import com.harunaga.games.common.client.miscellaneous.SizeFixedButton;
import com.harunaga.games.tetris.TetrisGameConfig;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import org.apache.log4j.Logger;

/**
 *
 * @author Harunaga
 */
public class LoginBox extends JPanel implements ActionListener {

    public final static String LOG_IN = "l";
    public final static String CANCEL = "c";
    JTextField ipBox;
    JTextField nameBox;
    JTextField pwBox;
    JLabel ipLabel;
    JLabel nameLabel;
    JLabel pwLabel;
    SizeFixedButton loginBt;
    SizeFixedButton cancelBt;
    ActionListener inputListener;
    Logger log = Logger.getLogger("LoginBox");

    /**
     * the listener will be call if login event is fired
     * @param listener
     */
    public void setListener(ActionListener listener) {
        inputListener = listener;
    }

    public String getIP() {
        return ipBox.getText();
    }

    public String getUsername() {
        return nameBox.getText();
    }

    public String getPassword() {
        return pwBox.getText();
    }

    public LoginBox(ActionListener listener, ImageIcon icon, ImageIcon rolloverIcon) {
        inputListener = listener;
        pwLabel = new JLabel("Password");
        nameLabel = new JLabel("UserName");
        ipLabel = new JLabel("IP");
        setLayout(new BorderLayout(10, 10));
        JPanel inforPane = new JPanel(new SpringLayout());
        ipBox = new HTextField(20);
        ipBox.setText("192.168.1.154");
        nameBox = new HTextField(40);
        pwBox = new HTextField(40);
        inforPane.add(ipLabel);
        inforPane.add(ipBox);
        inforPane.add(nameLabel);
        inforPane.add(nameBox);
        inforPane.add(pwLabel);
        inforPane.add(pwBox);
        SpringUtilities.makeCompactGrid(inforPane, 3, 2, 6, 6, 6, 6);
        add(inforPane, BorderLayout.CENTER);
        //confirm panel
        cancelBt = new SizeFixedButton("Cancel") {

            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                if (e.getID() == FocusEvent.FOCUS_LOST && isShowing()) {
                    ipBox.requestFocus();
                }
            }
        };
        cancelBt = ComponentProvider.createButton("Cancel", icon, rolloverIcon);
        cancelBt.setFont(Font.getFont(TetrisGameConfig.FONT_BUTTON_24));
        loginBt = ComponentProvider.createButton("Log in", icon, rolloverIcon);
        loginBt.setFont(Font.getFont(TetrisGameConfig.FONT_BUTTON_24));
        JPanel confirmPane = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        confirmPane.add(loginBt);
        confirmPane.add(cancelBt);
        add(confirmPane, BorderLayout.SOUTH);
        loginBt.addActionListener(this);
        cancelBt.addActionListener(this);
        setBorder(BorderFactory.createRaisedBevelBorder());
        setSize(getPreferredSize());
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        JPanel p = new JPanel();
//        p.add(new LoginBox(null, null));
//        //frame.setDefaultLookAndFeelDecorated(true);
//        p.add(new JButton("test"));
//        frame.add(p);
//        frame.pack();
//        //      frame.setResizable(false);
//        frame.setVisible(true);
//    }
    public synchronized void actionPerformed(ActionEvent e) {
        ActionListener listener = inputListener;
        if (listener != null) {
            if (e.getSource() == loginBt) {
                listener.actionPerformed(new ActionEvent(this, e.getID(), LOG_IN, e.getWhen(), e.getModifiers()));
            } else {
                listener.actionPerformed(new ActionEvent(this, e.getID(), CANCEL, e.getWhen(), e.getModifiers()));
            }
        }
    }

//    @Override
//    public void setEnabled(boolean enabled) {
//        super.setEnabled(enabled);
//        loginBt.setEnabled(enabled);
//        cancelBt.setEnabled(enabled);
//    }
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            ipBox.requestFocusInWindow();
        }
    }
}
