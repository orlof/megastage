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
        public static final String CELESTIAL = "celestial";
        public static final String USABLE = "usable";
    }
}

