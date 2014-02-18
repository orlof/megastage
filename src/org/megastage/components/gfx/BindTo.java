/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.gfx;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.client.ClientGlobals;
import org.megastage.protocol.Network;
import org.megastage.util.ID;

/**
 * This entity's position and rotation are relative to parent
 * @author Orlof
 */
public class BindTo extends BaseComponent {
    public int parent; 
    private transient boolean dirty = false;
    
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws Exception {
        this.parent = parent.id;
        
        return null;
    }

    @Override
    public boolean replicate() {
        return true;
    }

    @Override
    public boolean synchronize() {
        return dirty;
    }

    @Override
    public Network.ComponentMessage create(Entity entity) {
        dirty = false;
        return super.create(entity);
    }

    public void setParent(int eid) {
        this.dirty = true;
        this.parent = eid;
    }
    
    @Override
    public void receive(Connection pc, Entity entity) {
        Entity parentEntity = ClientGlobals.artemis.toClientEntity(parent);

        parent = parentEntity.id;

        //Log.info(ID.get(entity));
        //Log.info(ID.get(parentEntity));
        
        if(ClientGlobals.playerEntity == entity) {
            ClientGlobals.spatialManager.changeShip(parentEntity);
        } else {
            ClientGlobals.spatialManager.bindTo(parentEntity, entity);
        }
    }

    @Override
    public String toString() {
        return "BindTo(serverID=" + parent + ")";
    }
}
