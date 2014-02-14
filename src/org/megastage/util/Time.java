package org.megastage.util;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:50
 */
public class Time {
    public static long value = System.currentTimeMillis();
    
    public static double secs() {
        return value / 1000.0;
    }
}

