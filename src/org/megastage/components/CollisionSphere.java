package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class CollisionSphere extends BaseComponent {
    public double radius;

    public CollisionSphere() {}
    
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        radius = getDoubleValue(element, "radius", 30.0);
        return null;
    }

    @Override
    public String toString() {
        return "CollisionSphere(radius=" + radius + ")";
    }
}
