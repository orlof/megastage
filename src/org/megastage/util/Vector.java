package org.megastage.util;

import org.megastage.components.Position;

/**
 * Created with IntelliJ IDEA. User: contko3 Date: 8/14/13 Time: 8:35 AM To
 * change this template use File | Settings | File Templates.
 */
public class Vector {

    public static final Vector ZERO = new Vector();
    public static Vector UNIT_X = new Vector(1.0d, 0.0d, 0.0d);
    public static Vector UNIT_Y = new Vector(0.0d, 1.0d, 0.0d);
    public static Vector UNIT_Z = new Vector(0.0d, 0.0d, 1.0d);
    public final double x, y, z;

    public Vector() {
        this.x = 0.0d;
        this.y = 0.0d;
        this.z = 0.0d;
    }

    // create a new object with the given components
    public Vector(double x0, double y0, double z0) {
        this.x = x0;
        this.y = y0;
        this.z = z0;
    }

    // create a new object with the given components
    public Vector(Quaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
    }
    
    public Vector(Position pos) {
        this.x = pos.x / 1000;
        this.y = pos.y / 1000;
        this.z = pos.z / 1000;
    }

    // return a string representation of the invoking object
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }

    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y, z + v.z);
    }
    
    public Vector sub(Vector v) {
        return new Vector(x - v.x, y - v.y, z - v.z);
    }
    
    public Vector negate() {
        return new Vector(-x, -y, -z);
    }

    public Vector add(double dx, double dy, double dz) {
        return new Vector(x + dx, y + dy, z + dz);
    }

    public Vector sub(double dx, double dy, double dz) {
        return new Vector(x - dx, y - dy, z - dz);
    }

    // return the vector norm
    public Vector normalize(double tolerance) {
        double mag2 = x * x + y * y + z * z;
        if (Math.abs(mag2 - 1.0) > tolerance) {
            double mag = Math.sqrt(mag2);
            return new Vector(x / mag, y / mag, z / mag);
        }
        return this;
    }

    // return the quaternion norm
    public Vector normalize() {
        return normalize(0.00001d);
    }

    // return a vector rotated by a Quaternion (q * v * c(q))
    public Vector multiply(Quaternion q) {
        //Quaternion q2 = new Quaternion(normalize());
        Quaternion q2 = new Quaternion(this);
        return new Vector(q.multiply(q2).multiply(q.conjugate()));
    }

    public Vector multiply(double scalar) {
        return new Vector(x * scalar, y * scalar, z * scalar);
    }

    public Vector divide(double scalar) {
        return new Vector(x / scalar, y / scalar, z / scalar);
    }
    
    public double dot(Vector v){
        return x * v.x + y * v.y + z * v.z;
    }
 
    public Vector cross(Vector v){
        return new Vector(
            y * v.z - z * v.y,
            z * v.x - x * v.z,
            x * v.y - y * v.x
        );
    }
 
    public double length() {
        return Math.sqrt(lengthSquared());
    }
 
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }
    
    public double distance(Vector point) {
        Vector numerator = cross(point.negate());
        return numerator.length() / length();
    }

    public static void main(String[] args) throws Exception {
        Vector v = new Vector(1, 1, 0);
        Vector p = new Vector(10, 0, 0);
        
        System.out.println(v.distance(p));
    }

    public double distanceSquared(Vector pos) {
        double dx = Math.abs(pos.x - x);
        double dy = Math.abs(pos.y - y);
        double dz = Math.abs(pos.z - z);
        
        return dx*dx + dy*dy + dz*dz;
    }

    public double distanceSquared(Position pos) {
        double dx = Math.abs(pos.x - x) / 1000.0;
        double dy = Math.abs(pos.y - y) / 1000.0;
        double dz = Math.abs(pos.z - z) / 1000.0;
        
        return dx*dx + dy*dy + dz*dz;
    }
}
