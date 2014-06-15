package org.megastage.components.gfx;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class SunGeometry extends GeometryComponent {
    public float radius;
    public float red, green, blue, alpha;
    public float lightRadius;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        radius = (float) (getFloatValue(element, "radius", 10.0f));
        lightRadius = (float) (getFloatValue(element, "light_radius", 2000000.0f));
        red = getFloatValue(element, "red", 1.0f); 
        green = getFloatValue(element, "green", 1.0f); 
        blue = getFloatValue(element, "blue", 1.0f); 
        alpha = getFloatValue(element, "alpha", 1.0f); 
        
        return null;
    }
}
