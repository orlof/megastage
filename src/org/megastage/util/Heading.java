package org.megastage.util;

/**
 * Created by IntelliJ IDEA.
 * User: Teppo
 * Date: 17.8.2013
 * Time: 19:29
 * To change this template use File | Settings | File Templates.
 */
public class Heading {
    Heading parent;
    Quaternion total = new Quaternion();

    public Heading() {
        this(null);
    }

    public Heading(Heading parent) {
        this.parent = parent;
    }

    public Quaternion getGlobalRotation() {
        if(parent == null) return total;
        return total.multiply(parent.getGlobalRotation());
    }

    public void rotate(Vector axis, double radians_angle) {
        // rotate axis to global coordinate system
        Vector globalAxis = axis.multiply(getGlobalRotation());

        // rotation increment in global coordinate system
        Quaternion globalRotation = new Quaternion(globalAxis, radians_angle);

        // quaternion for the new coordinate system
        total = globalRotation.multiply(total);

        debug();
    }

    public void pitch(double degrees_up) {
        System.out.println("Heading.pitch");
        rotate(new Vector(1.0d, 0.0d, 0.0d), Math.toRadians(degrees_up));
    }

    public void roll(double degrees_cw) {
        System.out.println("Heading.roll");
        rotate(new Vector(0.0d, 0.0d, 1.0d), -Math.toRadians(degrees_cw));
    }

    public void yaw(double degrees_right) {
        System.out.println("Heading.yaw");
        rotate(new Vector(0.0d, 1.0d, 0.0d), -Math.toRadians(degrees_right));
    }

    public void debug() {
        Vector x = new Vector(1.0d, 0.0d, 0.0d).multiply(getGlobalRotation());
        System.out.println("x = " + x);
        Vector y = new Vector(0.0d, 1.0d, 0.0d).multiply(getGlobalRotation());
        System.out.println("y = " + y);
        Vector z = new Vector(0.0d, 0.0d, 1.0d).multiply(getGlobalRotation());
        System.out.println("z = " + z);
    }
}
