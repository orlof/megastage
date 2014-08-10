package org.megastage.components.srv;

import org.megastage.ecs.BaseComponent;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.World;

public class CollisionSphere extends BaseComponent {
    public float radius;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        radius = getFloatValue(element, "radius", 30.0f);
        return null;
    }
    
    public static float getRadius(int eid) throws ECSException {
        CollisionSphere cs = (CollisionSphere) World.INSTANCE.getComponentOrError(eid, CompType.CollisionSphere);
        return cs.radius;
    }
}
