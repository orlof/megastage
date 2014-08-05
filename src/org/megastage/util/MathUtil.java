package org.megastage.util;

import com.jme3.math.Vector3f;

public class MathUtil {
    public static float distancePointToLine(Vector3f point, Vector3f line) {
        Vector3f numerator = line.cross(point.negate());
        return numerator.length() / line.length();
    }
}
