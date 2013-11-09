package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.Vector;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Position extends BaseComponent {
    public long x, y, z;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        x = getLongValue(element, "x", 0);
        y = getLongValue(element, "y", 0);
        z = getLongValue(element, "z", 0);
    }

    public void add(Vector vector) {
        x += Math.round(vector.x);
        y += Math.round(vector.y);
        z += Math.round(vector.z);
    }

    public void move(Velocity velocity, float time) {
        add(velocity.getPositionChange(time));
    }
    
    public Vector3f getAsVector() {
        return new Vector3f(x / 1000.0f, y / 1000.0f, z / 1000.0f);
    }
    
    public String toString() {
        return "Position(" + x + ", " + y + ", " + z + ")";
    }
}
