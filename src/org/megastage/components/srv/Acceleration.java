package org.megastage.components.srv;

import org.megastage.components.BaseComponent;
import org.megastage.util.Vector;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Acceleration extends BaseComponent {
    public Vector vector = Vector.ZERO;

    public void add(Vector v) {
        vector = vector.add(v);
    }

    public void add(double ax, double ay, double az) {
        vector = vector.add(ax, ay, az);
    }

    public void set(Vector v) {
        vector = v;
    }

    public Vector getVelocityChange(float time) {
        return vector.multiply(time);
    }

    public String toString() {
        return "Acceleration(" + vector.toString() + ")";
    }
}
