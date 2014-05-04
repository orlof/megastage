/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.ecs.World;

/**
 *
 * @author Orlof
 */
public interface Message {
    public void receive(World world, Connection pc);
}
