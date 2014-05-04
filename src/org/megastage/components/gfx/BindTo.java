package org.megastage.components.gfx;

import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

public class BindTo extends BaseComponent {
    public int parent; 
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        this.parent = parentEid;
        
        return null;
    }

    @Override
    public Message replicate(int eid) {
        return always(eid);
    }
    
    @Override
    public Message synchronize(int eid) {
        return ifDirty(eid);
    }

    public void setParent(int eid) {
        this.dirty = true;
        this.parent = eid;
    }
    
    @Override
    public void receive(World world, Connection pc, int eid) {
        if(ClientGlobals.playerEntity == eid) {
            ClientGlobals.spatialManager.changeShip(parent);
        } else {
            ClientGlobals.spatialManager.bindTo(parent, eid);
        }
    }

    @Override
    public String toString() {
        return "BindTo(serverID=" + parent + ")";
    }
}
