package org.megastage.components.gfx;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class MonitorGeometry extends GeometryComponent {
    public float width, height;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        width = getFloatValue(element, "width", 3.0f);
        height = getFloatValue(element, "height", 2.0f);
        
        return null;
    }
}
