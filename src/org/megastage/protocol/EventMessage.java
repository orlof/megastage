/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.systems.ClientNetworkSystem;

/**
 *
 * @author contko3
 */
public abstract class EventMessage implements Message {
    @Override
    public void receive(Connection pc) {}
}
    
