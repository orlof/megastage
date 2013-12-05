package org.megastage.util;

import com.artemis.Entity;
import com.cubes.CubesSettings;
import com.jme3.scene.Node;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:50
 */
public class ClientGlobals {
    public static Entity fixedEntity = null;
    public static double scale = 1000.0;
    
    public static long time = System.currentTimeMillis();
    public static long timeDiff;

    public static Node rootNode;
    public static final Node sysRotNode = new Node("system_rotation_node");
    public static final Node sysMovNode = new Node("system_move_node");

    public static GFXQuality gfxQuality = new GFXLowQuality();
    public static CubesSettings cubesSettings;
    public static Node sceneNode;
    
    public static class GFXQuality {
        public static int SPHERE_Z_SAMPLES = 32;
        public static int SPHERE_RADIAL_SAMPLES = 32;
        public static int PLANET_PLANAR_QUADS_PER_PATCH = 32;
        public static boolean PLANET_SHADOWS_ENABLED = true;
        public static int SCREEN_WIDTH = 800;
        public static int SCREEN_HEIGHT = 600;
        public static boolean ENABLE_LEM_BLINKING = true;
    }
    
    public static class GFXLowQuality extends GFXQuality {
        public static int Z1 = SPHERE_Z_SAMPLES = 8;
        public static int Z2 = SPHERE_RADIAL_SAMPLES = 8;
        public static int Z3 = PLANET_PLANAR_QUADS_PER_PATCH = 8;
        public static boolean Z4 = PLANET_SHADOWS_ENABLED = false;
        public static int Z5 = SCREEN_WIDTH = 320;
        public static int Z6 = SCREEN_HEIGHT = 200;
        public static boolean Z7 = ENABLE_LEM_BLINKING = false;
    }
    
}

