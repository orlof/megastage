/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.protocol.Network;
import org.megastage.systems.ClientNetworkSystem;

/**
 *
 * @author Teppo
 */
public abstract class EntityComponent extends BaseComponent {
    @Override
    public Network.EntityData create(Entity entity) {
        return new Network.EntityData(entity, this);
    }
    
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        system.cems.setComponent(entity, this);
    }
}
