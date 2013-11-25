package org.megastage.util;

/*************************************************************************
 *  Compilation:  javac Quaternion.java
 *  Execution:    java Quaternion
 *
 *  Data type for quaternions.
 *
 *  http://mathworld.wolfram.com/Quaternion.html
 *
 *  The data type is "immutable" so once you create and initialize
 *  a Quaternion, you cannot change it.
 *
 *  % java Quaternion
 *
 *************************************************************************/

public class Quaternion {
    public final double w, x, y, z;

    public Quaternion() {
        this.w = 1.0d;
        this.x = 0.0d;
        this.y = 0.0d;
        this.z = 0.0d;
    }

    // create a new object with the given components
    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion(Vector v) {
        this.w = 0.0d;
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Quaternion(Vector axis, double angle) {
        Vector v = axis.normalize();
        angle /= 2.0d;
        w = Math.cos(angle);
        x = v.x * Math.sin(angle);
        y = v.y * Math.sin(angle);
        z = v.z * Math.sin(angle);
    }

    // return a string representation of the invoking object
    public String toString() {
        return w + " + " + x + "i + " + y + "j + " + z + "k";
    }

    public double getAngle() {
        return 2.0d * Math.acos(w);
    }

    // return the quaternion norm
    public double norm() {
        return Math.sqrt(w * w + x * x + y * y + z * z);
    }

    public Quaternion normalize(double tolerance) {
        double mag2 = w * w + x * x + y * y + z * z;
        if(Math.abs(mag2 - 1.0) > tolerance) {
            double mag = Math.sqrt(mag2);
            return new Quaternion(w /mag, x /mag, y /mag, z /mag);
        }
        return this;
    }

    public Quaternion normalize() {
        return normalize(0.00001d);
    }

    // return the quaternion conjugate
    public Quaternion conjugate() {
        //Quaternion q = normalize();
        Quaternion q = this;
        return new Quaternion(q.w, -q.x, -q.y, -q.z);
    }

    // return a new Quaternion whose value is (this + b)
    public Quaternion plus(Quaternion b) {
        Quaternion a = this;
        return new Quaternion(a.w +b.w, a.x +b.x, a.y +b.y, a.z +b.z);
    }


    // return a new Quaternion whose value is (this * b)
    public Quaternion multiply(Quaternion b) {
        Quaternion a = this;
        double w1 = a.w*b.w - a.x*b.x - a.y*b.y - a.z*b.z;
        double x1 = a.w*b.x + a.x*b.w + a.y*b.z - a.z*b.y;
        double y1 = a.w*b.y - a.x*b.z + a.y*b.w + a.z*b.x;
        double z1 = a.w*b.z + a.x*b.y - a.y*b.x + a.z*b.w;
        return new Quaternion(w1, x1, y1, z1);
    }

    // return a new Quaternion whose value is the inverse of this
    public Quaternion inverse() {
        double d = w * w + x * x + y * y + z * z;
        return new Quaternion(w /d, -x /d, -y /d, -z /d);
    }

    // return a / b
    public Quaternion divide(Quaternion b) {
        Quaternion a = this;
        return a.inverse().multiply(b);
    }
    
    public Quaternion localRotation(Vector axis, double radians_angle) {
        Vector globalAxis = axis.multiply(this);
        Quaternion rotation = new Quaternion(globalAxis, radians_angle);
        return rotation.multiply(this);
    }
}
