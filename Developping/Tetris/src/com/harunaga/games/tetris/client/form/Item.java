/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.tetris.client.form;

import com.harunaga.games.common.client.miscellaneous.SizeFixedButton;
import java.awt.event.ActionListener;

/**
 *
 * @author Harunaga
 */
public class Item extends SizeFixedButton {

    String value;

    public Item(String key, String value) {
        super(key);
        this.value = value;
        setIgnoreRepaint(true);
        setBorder(null);
        setContentAreaFilled(false);
        setFocusable(false);
    }
}
