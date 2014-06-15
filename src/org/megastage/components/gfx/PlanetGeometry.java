package org.megastage.components.gfx;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class PlanetGeometry extends GeometryComponent {
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
}
