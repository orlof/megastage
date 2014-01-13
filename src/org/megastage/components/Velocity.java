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
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        double x = 1000 * getDoubleValue(element, "x", 0);
        double y = 1000 * getDoubleValue(element, "y", 0);
        double z = 1000 * getDoubleValue(element, "z", 0);
        
        vector = new Vector(x, y, z);
        
        return null;
    }

    public void add(Vector v) {
        vector = vector.add(v);
    }

    public Vector getPositionChange(float time) {
        return vector.multiply(time);
    }

    public void accelerate(Acceleration acceleration, float time) {
        vector = vector.add(acceleration.getVelocityChange(time));
    }

    public String toString() {
        return "Velocity(" + vector.toString() + ")";
    }
}
