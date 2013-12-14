/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.server;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.components.EntityComponent;
import org.megastage.protocol.Network;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.util.ClientGlobals;

/**
 *
 * @author Teppo
 */
public class BindTo extends EntityComponent {
    public int entityID; 
    
    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        entityID = parent.getId();
    }

    @Override
    public Network.EntityData create(Entity entity) {
        return new Network.EntityData(entity, this);
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc, Entity entity) {
        Entity parent = ClientGlobals.artemis.get(entityID);

        if(ClientGlobals.playerEntity == entity) {
            ClientGlobals.spatialManager.changeShip(parent);
        } else {
            ClientGlobals.spatialManager.bindTo(parent, entity);
        }
    }

    public String toString() {
        return "BindTo(serverID=" + entityID + ")";
    }
}
