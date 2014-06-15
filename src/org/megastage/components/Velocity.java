package org.megastage.components;

import org.megastage.ecs.BaseComponent;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.srv.Acceleration;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
import org.megastage.util.Vector3d;

public class Velocity extends ReplicatedComponent {
    public Vector3d vector;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        double x = getDoubleValue(element, "x", 0);
        double y = getDoubleValue(element, "y", 0);
        double z = getDoubleValue(element, "z", 0);
        
        vector = new Vector3d(x, y, z);
        
        return null;
    }

    public void add(Vector3d v) {
        vector = vector.add(v);
    }

    public Vector3d getPositionChange(float time) {
        double multiplier = 1000.0 * time;
        return vector.multiply(multiplier);
    }

    public void accelerate(Acceleration acceleration, float time) {
        vector = vector.add(acceleration.getVelocityChange(time));
    }
}
