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
public class Rotation extends BaseComponent {
    Entity parent;
    Quaternion total = new Quaternion();

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        this.parent = parent;
    }

    public Quaternion getOrientation() {
        if(parent == null) return total;

        Rotation parentRotation = parent.getComponent(Rotation.class);
        if(parentRotation == null) return total;

        return total.multiply(parentRotation.getOrientation());
    }

    public void rotate(Vector axis, double radians_angle) {
        // rotate axis to global coordinate system
        Vector globalAxis = axis.multiply(getOrientation());

        // rotation increment in global coordinate system
        Quaternion globalRotation = new Quaternion(globalAxis, radians_angle);

        // quaternion for the new coordinate system
        total = globalRotation.multiply(total);

        debug();
    }

    public void pitch(double degrees_up) {
        System.out.println("Rotation.pitch");
        rotate(Vector.UNIT_X, Math.toRadians(degrees_up));
    }

    public void roll(double degrees_cw) {
        System.out.println("Rotation.roll");
        rotate(Vector.UNIT_Z, -Math.toRadians(degrees_cw));
    }

    public void yaw(double degrees_right) {
        System.out.println("Rotation.yaw");
        rotate(Vector.UNIT_Y, -Math.toRadians(degrees_right));
    }

    public void debug() {
        Vector x = new Vector(1.0d, 0.0d, 0.0d).multiply(getOrientation());
        System.out.println("x = " + x);
        Vector y = new Vector(0.0d, 1.0d, 0.0d).multiply(getOrientation());
        System.out.println("y = " + y);
        Vector z = new Vector(0.0d, 0.0d, 1.0d).multiply(getOrientation());
        System.out.println("z = " + z);
    }


}
