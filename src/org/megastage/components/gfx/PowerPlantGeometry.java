package org.megastage.components.gfx;

import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
    
public class PowerPlantGeometry extends BaseComponent {
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        return null;
    }

    @Override
    public void receive(World world, Connection pc, int eid) {
        ClientGlobals.spatialManager.setupPowerPlant(eid, this);
    }
    
    @Override
    public Message replicate(int eid) {
        return always(eid);
    }
}
