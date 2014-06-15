package org.megastage.components.srv;

import org.megastage.ecs.BaseComponent;
import org.megastage.util.Vector3d;

public class Acceleration extends BaseComponent {
    public Vector3d vector = Vector3d.ZERO;

    public void add(Vector3d v) {
        vector = vector.add(v);
    }

    public void add(double ax, double ay, double az) {
        vector = vector.add(ax, ay, az);
    }

    public void set(Vector3d v) {
        vector = v;
    }

    public Vector3d getVelocityChange(float time) {
        return vector.multiply(time);
    }
}
