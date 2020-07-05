/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.harunaga.games.common.client.net;

import com.harunaga.games.common.GameEvent;

/**
 *
 * @author Harunaga
 */
public interface GameEventListener {

    public void processEvent(GameEvent e);
}
