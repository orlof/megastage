package org.megastage.components.gfx;

import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

public class MonitorGeometry extends BaseComponent {
    public float width, height;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        width = getFloatValue(element, "width", 3.0f);
        height = getFloatValue(element, "height", 2.0f);
        
        return null;
    }

    @Override
    public Message replicate(int eid) {
        return always(eid);
    }
    
    @Override
    public void receive(World world, Connection pc, int eid) {
        ClientGlobals.spatialManager.setupMonitor(eid, this);
    }
}
