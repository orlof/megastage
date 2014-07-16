package org.megastage.util;

import com.jme3.math.Vector3f;
import org.megastage.components.Position;

public class Vector3d {
    public static transient final boolean CAN_SAVE = true;

    public static transient final Vector3d ZERO = new Vector3d();
    public static transient Vector3d UNIT_X = new Vector3d(1.0d, 0.0d, 0.0d);
    public static transient Vector3d UNIT_Y = new Vector3d(0.0d, 1.0d, 0.0d);
    public static transient Vector3d UNIT_Z = new Vector3d(0.0d, 0.0d, 1.0d);
    public static transient Vector3d FORWARD = new Vector3d(0.0d, 0.0d, -1.0d);
    public final double x, y, z;

    public Vector3d() {
        this.x = 0.0d;
        this.y = 0.0d;
        this.z = 0.0d;
    }

    // create a new object with the given components
    public Vector3d(double x0, double y0, double z0) {
        this.x = x0;
        this.y = y0;
        this.z = z0;
    }

    // create a new object with the given components
    public Vector3d(Quaternion2 q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
    }
    
    public Vector3d(Vector3d vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vector3d(Vector3f vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    // return a string representation of the invoking object
    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }

    public Vector3d add(Vector3d v) {
        return new Vector3d(x + v.x, y + v.y, z + v.z);
    }
    
    public Vector3d sub(Vector3d v) {
        return new Vector3d(x - v.x, y - v.y, z - v.z);
    }
    
    public Vector3d negate() {
        return new Vector3d(-x, -y, -z);
    }

    public Vector3d add(double dx, double dy, double dz) {
        return new Vector3d(x + dx, y + dy, z + dz);
    }

    public Vector3d sub(double dx, double dy, double dz) {
        return new Vector3d(x - dx, y - dy, z - dz);
    }

    // return the vector norm
    public Vector3d normalize(double tolerance) {
        double mag2 = x * x + y * y + z * z;
        if (Math.abs(mag2 - 1.0) > tolerance) {
            double mag = Math.sqrt(mag2);
            return new Vector3d(x / mag, y / mag, z / mag);
        }
        return this;
    }

    // return the quaternion norm
    public Vector3d normalize() {
        return normalize(0.00001d);
    }

    // return a vector rotated by a Quaternion (q * v * c(q))
    public Vector3d multiply(Quaternion2 q) {
        //Quaternion q2 = new Quaternion(normalize());
        Quaternion2 q2 = new Quaternion2(this);
        return new Vector3d(q.multiply(q2).multiply(q.conjugate()));
    }

    public Vector3d multiply(double scalar) {
        return new Vector3d(x * scalar, y * scalar, z * scalar);
    }

    public Vector3d divide(double scalar) {
        return new Vector3d(x / scalar, y / scalar, z / scalar);
    }
    
    public double dot(Vector3d v){
        return x * v.x + y * v.y + z * v.z;
    }
 
    public Vector3d cross(Vector3d v){
        return new Vector3d(
            y * v.z - z * v.y,
            z * v.x - x * v.z,
            x * v.y - y * v.x
        );
    }
 
    public double distanceToPoint(Vector3d point) {
        Vector3d numerator = cross(point.negate());
        return numerator.length() / length();
    }

    public double distanceToPointSquared(Vector3d point) {
        Vector3d numerator = cross(point.negate());
        return numerator.lengthSquared() / lengthSquared();
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }
 
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }
    
    public double distance(Vector3d coord) {
        return Math.sqrt(distanceSquared(coord));
    }
    
    public double distanceSquared(Vector3d pos) {
        double dx = pos.x - x;
        double dy = pos.y - y;
        double dz = pos.z - z;
        
        return dx*dx + dy*dy + dz*dz;
    }

    public Vector3f getVector3f() {
        return new Vector3f((float) x, (float) y, (float) z);
    }
    
    public static void main(String args[]) throws Exception {
        Vector3d line = new Vector3d(1, 0, 0);
        long start = System.currentTimeMillis();
        for(int i=0;i<1000000;i++)  
            line.distanceToPoint(new Vector3d(10,2,0));
        System.out.println(System.currentTimeMillis() - start);
    }



} 
