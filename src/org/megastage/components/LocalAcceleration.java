package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class LocalAcceleration extends BaseComponent {
    public Vector vector = Vector.ZERO;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
    }

    public void add(Vector v) {
        vector = vector.add(v);
    }

    public void add(double ax, double ay, double az) {
        vector = vector.add(ax, ay, az);
    }

    public void set(Vector v) {
        vector = v;
    }

    public Vector getVelocityChange(Quaternion heading, float time) {
        // calculate speed change in local coordinate space
        Vector localVelocityChange = vector.multiply(-1.0d * 1000.0d * time);

        // convert speed change from local to global -space
        return localVelocityChange.multiply(heading);
    }
}
