package org.megastage.components.gfx;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.components.BaseComponent;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

public class VoidGeometry extends BaseComponent {
    @Override
    public Message replicate(int eid) {
        return always(eid);
    }
    
    @Override
    public void receive(World world, Connection pc, int eid) {
        ClientGlobals.spatialManager.setupVoidNode(eid, this);
    }
    
    @Override
    public String toString() {
        return "VoidGeometry()";
    }
}
