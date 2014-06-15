package org.megastage.components.gfx;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.World;

public class CharacterGeometry extends GeometryComponent {
    public float red, green, blue, alpha;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        red = getFloatValue(element, "red", 1.0f); 
        green = getFloatValue(element, "green", 1.0f); 
        blue = getFloatValue(element, "blue", 1.0f); 
        alpha = getFloatValue(element, "alpha", 1.0f); 
        
        return null;
    }

    @Override
    public void receive(int eid) {
        World.INSTANCE.setComponent(eid, this);
        if(eid != ClientGlobals.playerEntity) {
            ClientGlobals.spatialManager.setupGeometry(eid, this);
        }
    }
}
