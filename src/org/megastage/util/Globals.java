package org.megastage.util;

import com.artemis.Entity;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:50
 */
public class Globals {
    public static Entity fixedEntity = null;
    public static double scale = 1000.0;
    
    public static double G = 6.67384e-11;
    public static String networkInterface = "localhost";

    public static String serverHost = "localhost";
    public static int serverPort = 12358;

    public static int clientPort = 0;

    public static long time = System.currentTimeMillis();
    public static long timeDiff;

    public class Group {
        public static final String CELESTIAL = "celestial";
        public static final String USABLE = "usable";
    }
}

