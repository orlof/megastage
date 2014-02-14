package org.megastage.components.srv;

import org.megastage.components.BaseComponent;
import org.megastage.util.Vector3d;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Acceleration extends BaseComponent {
    public Vector3d vector = Vector3d.ZERO;

    public Acceleration() {
        super();
    }

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

    public String toString() {
        return "Acceleration(" + vector.toString() + ")";
    }
}
