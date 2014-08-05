package org.megastage.components.srv;

import org.megastage.ecs.BaseComponent;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.World;

public class CollisionSphere extends BaseComponent {
    public double radius;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        radius = getDoubleValue(element, "radius", 30.0);
        return null;
    }
}
