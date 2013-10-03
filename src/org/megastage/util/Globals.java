package org.megastage.util;

import java.util.HashMap;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:50
 */
public class Globals {
    public static double G = 6.67384e-11;
    public static String networkInterface = "localhost";

    public static String serverHost = "localhost";
    public static int serverPort = 12358;

    public static int clientPort = 0;

    public static long time = System.currentTimeMillis();

    public class Group {
        public static final String GRAVITY_WELLS = "gravity well";
        public static final String USABLE = "can_use";
    }
    
    public class Tag {
        public static final String IN_USE = "IN_USE";
        public static final String CAN_USE = "can_use";
    }

}
