package org.megastage.server;

import com.jme3.math.Vector3f;

public class ServerGlobals {
    public static boolean autoexit;
    public static Vector3f shipStartVec = new Vector3f();

    public static void advanceShipStartVec() {
        shipStartVec.addLocal(50.0f, 0.0f, 0.0f);
    }
}
