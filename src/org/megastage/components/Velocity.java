package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.Vector;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Velocity extends BaseComponent {
    public Vector vector;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        double x = element.getAttribute("x").getDoubleValue();
        double y = element.getAttribute("y").getDoubleValue();
        double z = element.getAttribute("z").getDoubleValue();
        
        vector = new Vector(x, y, z);
    }

    public void add(Vector v) {
        vector = vector.add(v);
    }

}
