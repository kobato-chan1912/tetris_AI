package com.harunaga.games.tetris.client.form;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.harunaga.games.common.client.font.Font;
import com.harunaga.games.common.client.miscellaneous.SizeFixedButton;
import com.harunaga.games.common.client.resource.ResourceManager;
import com.harunaga.games.tetris.TetrisGameConfig;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;

/**
 *
 * @author Harunaga
 */
public class ComponentProvider {

    ResourceManager resource;

    public ComponentProvider(ResourceManager resource) {
        this.resource = resource;
    }

    public SizeFixedButton createTranslucentButton(String name) {
        // load the image
        String imagePath = "buttons/" + name + ".png";
        Image source = resource.getImage(imagePath);
        // create the button
        SizeFixedButton button = createButton(new ImageIcon(source),
                new ImageIcon(resource.getTranslucentImage(source, 0.5f)));
        button.setFocusable(false);
        return button;
    }

    public SizeFixedButton createTranslucentButton(String name, String tooltip,
            ActionListener listener, int cursorType) {
        SizeFixedButton button = createTranslucentButton(name, listener);
        // get the cursor for this button
        Cursor cursor = Cursor.getPredefinedCursor(cursorType);
        button.setCursor(cursor);
        button.setToolTipText(tooltip);
        return button;
    }

    public SizeFixedButton createTranslucentButton(String name, ActionListener listener) {
        SizeFixedButton button = createTranslucentButton(name);
        button.addActionListener(listener);
        button.setFocusable(false);
        return button;
    }

    public SizeFixedButton createOvalButton(String name, String rolloverBackgroundFile) {
        SizeFixedButton button = createButton(new ImageIcon(resource.getImage("buttons/" + name + ".png")),
                new ImageIcon(resource.getImage("buttons/" + rolloverBackgroundFile)));
        button.setDrawIconSeperately(false);
        button.setFocusable(false);
        return button;
    }

    public SizeFixedButton createOvalButton(String name, String rolloverBackgroundFile,
            String tooltip, ActionListener listener, int cursorType) {
        SizeFixedButton button = createOvalButton(name, rolloverBackgroundFile);
        button.addActionListener(listener);
        Cursor cursor = Cursor.getPredefinedCursor(cursorType);
        button.setCursor(cursor);
        button.setToolTipText(tooltip);
        return button;
    }

    public SizeFixedButton getDefaultButton(String text, ActionListener listener) {
        SizeFixedButton button = createButton(new ImageIcon(resource.getImage("buttons/button.png")),
                new ImageIcon(resource.getImage("buttons/button_rollover.png")));
        button.setHorizontalTextPosition(SizeFixedButton.LEFT);
        button.setFont(Font.getFont(TetrisGameConfig.FONT_BUTTON_24));
        button.setText(text);
        button.addActionListener(listener);
        return button;
    }

    public SizeFixedButton getDefaultButton(String text, String tooltip, ActionListener listener, int cursorType) {
        SizeFixedButton button = getDefaultButton(text, listener);
        button.setToolTipText(tooltip);
        button.setCursor(Cursor.getPredefinedCursor(cursorType));
        return button;
    }

    public static SizeFixedButton createButton(ImageIcon icon, ImageIcon rolloverIcon) {
        return createButton(null, icon, rolloverIcon);
    }

    public static SizeFixedButton createButton(String text, ImageIcon icon, ImageIcon rolloverIcon) {
        SizeFixedButton button = new SizeFixedButton(text);
        button.setIcon(icon);
        button.setRolloverIcon(rolloverIcon);
        button.setBorder(null);
        button.setBorderPainted(false);
        button.setIgnoreRepaint(true);
        return button;
    }

    public JCheckBox getDefaultCheckbox(String name, ActionListener listener, boolean selected) {
        JCheckBox check = new JCheckBox(name);
        check.setIcon(new ImageIcon(resource.getImage("checkbox/checkbox.png")));
        check.setSelectedIcon(new ImageIcon(resource.getImage("checkbox/checkbox_selected.png")));
        check.setRolloverIcon(new ImageIcon(resource.getImage("checkbox/checkbox_rollover.png")));
        check.setRolloverSelectedIcon(new ImageIcon(resource.getImage("checkbox/checkbox_selected_rollover.png")));
        check.addActionListener(listener);
        check.setSelected(selected);
        return check;
    }
}
