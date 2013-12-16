/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;

/**
 *
 * @author Orlof
 */
public interface Message {
    public void receive(Connection pc);
}
