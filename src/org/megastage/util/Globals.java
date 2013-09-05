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

    public static int clientPort = serverPort + 1;

    public static long time = System.currentTimeMillis();

    public class Group {
        public static final String GRAVITY_WELLS = "gravity well";
        public static final String CAN_USE = "can_use";
    }
    
    public class Tag {
        public static final String IN_USE = "IN_USE";
        public static final String CAN_USE = "can_use";
    }

    public class Message {
        public static final int LOGIN = 0;
        public static final int VIDEO_RAM = 1;
        public static final int KEY_TYPED = 2;
        public static final int ENTITY = 3;
        public static final int USE_ENTITY = 4;
        public static final int START_USE = 5;
        public static final int LOGOUT = 6;
        public static final int KEY_RELEASED = 7;
        public static final int KEY_PRESSED = 8;
    }
}
