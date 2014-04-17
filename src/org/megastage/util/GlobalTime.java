package org.megastage.util;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:50
 */
public class GlobalTime {
    public static long value = System.currentTimeMillis();
    private static long offset = 0;
    
    public static void set(long v) {
        value = v + offset;
    }
    
    public static double secs() {
        return value / 1000.0;
    }

    public static void setOffset(long o) {
        offset = o;
    }
    
}

