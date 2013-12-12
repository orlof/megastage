/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.systems.ClientNetworkSystem;

/**
 *
 * @author Teppo
 */
public interface Message {
    public void receive(ClientNetworkSystem system, Connection pc);
}
