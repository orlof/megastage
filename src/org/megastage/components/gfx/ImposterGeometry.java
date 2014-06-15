package org.megastage.components.gfx;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class ImposterGeometry extends GeometryComponent {
    public float radius;
    public double cutoff;
    public float red, green, blue, alpha;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        radius = getFloatValue(element, "radius", 20.0f);
        cutoff = getDoubleValue(element, "cutoff", 500000.0);
        red = getFloatValue(element, "red", 1.0f); 
        green = getFloatValue(element, "green", 1.0f); 
        blue = getFloatValue(element, "blue", 1.0f); 
        alpha = getFloatValue(element, "alpha", 1.0f); 
        
        return null;
    }

    @Override
    public void receive(int eid) {
        assert !World.INSTANCE.hasComponent(eid, CompType.ImposterGeometry);
        ClientGlobals.spatialManager.setupGeometry(eid, this);
    }
}
