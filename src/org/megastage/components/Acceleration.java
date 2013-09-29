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
public class Acceleration extends BaseComponent {
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

    public void add(double ax, double ay, double az) {
        vector = vector.add(ax, ay, az);
    }

    public void set(Vector v) {
        vector = v;
    }
    
    public long getCoordinateX() {
        return Math.round(vector.getX());
    }

    public long getCoordinateY() {
        return Math.round(vector.getY());
    }

    public long getCoordinateZ() {
        return Math.round(vector.getZ());
    }
}
