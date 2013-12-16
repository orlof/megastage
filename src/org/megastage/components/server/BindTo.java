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
import org.megastage.util.ClientGlobals;

/**
 * This entity's position and rotation are relative to parent
 * @author Orlof
 */
public class BindTo extends EntityComponent {
    public int parent; 
    
    @Override
    public void init(World world, Entity parent, Element element) throws Exception {
        this.parent = parent.getId();
    }

    @Override
    public Network.EntityData create(Entity entity) {
        return new Network.EntityData(entity, this);
    }

    @Override
    public void receive(Connection pc, Entity entity) {
        Entity parentEntity = ClientGlobals.artemis.toClientEntity(parent);

        parent = parentEntity.getId();

        if(ClientGlobals.playerEntity == entity) {
            ClientGlobals.spatialManager.changeShip(parentEntity);
        } else {
            ClientGlobals.spatialManager.bindTo(parentEntity, entity);
        }
    }

    public String toString() {
        return "BindTo(serverID=" + parent + ")";
    }
}
