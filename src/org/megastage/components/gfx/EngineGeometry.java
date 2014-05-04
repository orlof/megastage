package org.megastage.components.gfx;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
    
public class EngineGeometry extends BaseComponent {
    @Override
    public void receive(World world, Connection pc, int eid) {
        ClientGlobals.spatialManager.setupEngine(eid, this);
    }
    
    @Override
    public Message replicate(int eid) {
        return always(eid);
    }
}
