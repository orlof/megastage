package org.megastage.components.gfx;

import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

public class PlanetGeometry extends BaseComponent {
    public int center;
    public float radius;
    public String generator;
    public String color;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        center = parentEid;

        radius = getFloatValue(element, "radius", 10.0f);
        generator = getStringValue(element, "generator", "Earth");
        color = getStringValue(element, "color", "red");
        
        return null;
    }

    @Override
    public Message replicate(int eid) {
        return always(eid);
    }
    
    @Override
    public void receive(World world, Connection pc, int eid) {
        // center = ClientGlobals.artemis.get(center).getId();
        ClientGlobals.spatialManager.setupPlanetLikeBody(eid, this);
    }
    
    @Override
    public void delete(World world, Connection pc, int eid) {
        ClientGlobals.spatialManager.deleteEntity(eid);
        world.deleteEntity(eid);
    }
    
    @Override
    public String toString() {
        return "PlanetGeometry(center=" + center + ", generator='" + generator + "', radius=" + radius + ")";
    }
}
