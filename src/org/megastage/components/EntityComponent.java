/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.protocol.Network;

/**
 *
 * @author Orlof
 */
public abstract class EntityComponent extends BaseComponent {
    @Override
    public Network.ComponentMessage create(Entity entity) {
        return new Network.ComponentMessage(entity, this);
    }
    
    public void receive(Connection pc, Entity entity) {
        entity.addComponent(this);
    }
}
