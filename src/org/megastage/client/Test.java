/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import com.jme3.math.FastMath;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector;

/**
 *
 * @author teppo
 */
public class Test {
    private static Quaternion heading = new Quaternion();

    public static void main(String[] args) throws Exception {
        rotate(90, new Vector(1,0,0));
        print();
        
        rotate(90, new Vector(0,1,0));
        print();

        rotate(90, new Vector(0,0,1));
        print();

        rotate(90, new Vector(1,0,0));
        print();
        
        rotate(90, new Vector(0,1,0));
        print();

        rotate(90, new Vector(0,0,1));
        print();

    }

    private static void print() {
        Vector v = new Vector(0,0,-1);
        v = v.multiply(heading);
        System.out.println(v.toString());
    }
    
    private static void rotate(double value, Vector axis) {
        // rotate rotation axis by fixedEntity rotation
        Vector globalAxis = axis.multiply(heading);

        // rotation increment in global coordinate system
        Quaternion globalRotation = new Quaternion(globalAxis, rad(value));

        // quaternion for the new coordinate system
        heading = globalRotation.multiply(heading).normalize();
    }

    private static double rad(double deg) {
        return FastMath.DEG_TO_RAD * deg;
    }
}
