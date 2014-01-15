package org.megastage.util;

import com.artemis.Entity;
import com.cubes.CubesSettings;
import com.jme3.scene.Node;
import org.megastage.client.ArtemisState;
import org.megastage.client.Main;
import org.megastage.client.CommandHandler;
import org.megastage.client.SpatialManager;
import org.megastage.protocol.UserCommand;
import org.megastage.systems.ClientNetworkSystem;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:50
 */
public class ClientGlobals {
    public static ClientNetworkSystem network;
    
    public static Entity playerEntity;
    public static Entity shipEntity;
    
    public static long time = System.currentTimeMillis();
    public static long timeDiff;

    public static Node rootNode;
    public static Node sceneNode;
    public static final Node sysRotNode = new Node("system_rotation_node");
    public static final Node sysMovNode = new Node("system_move_node");
    public static final Node fixedNode = new Node("fixed_node");
    public static final Node playerNode = new Node("player");

    public static GFXQuality gfxQuality = new GFXQuality();
    public static double scale = 1000.0;
    public static CubesSettings cubesSettings;
    public static final UserCommand userCommand = new UserCommand();
    public static Main app;
    public static CommandHandler cmdHandler;
    public static SpatialManager spatialManager;
    public static ArtemisState artemis;
    public static String serverHost = "localhost";
    
    public static class GFXQuality {
        public static int SPHERE_Z_SAMPLES = 32;
        public static int SPHERE_RADIAL_SAMPLES = 32;
        public static int PLANET_PLANAR_QUADS_PER_PATCH = 32;
        public static boolean PLANET_FAR_FILTER_ENABLED = true;
        public static int SCREEN_WIDTH = 800;
        public static int SCREEN_HEIGHT = 600;
        public static boolean ENABLE_LEM_BLINKING = true;
        public static boolean ENABLE_PLANETS = true;
    }
    
    public static class GFXLowQuality extends GFXQuality {
        public static int Z1 = SPHERE_Z_SAMPLES = 6;
        public static int Z2 = SPHERE_RADIAL_SAMPLES = 6;
        public static int Z3 = PLANET_PLANAR_QUADS_PER_PATCH = 4;
        public static boolean Z8 = PLANET_FAR_FILTER_ENABLED = false;
        public static int Z5 = SCREEN_WIDTH = 640;
        public static int Z6 = SCREEN_HEIGHT = 400;
        public static boolean Z7 = ENABLE_LEM_BLINKING = false;
        public static boolean Z9 = ENABLE_PLANETS = true;
    }
    
    public static class GFXVeryLowQuality extends GFXQuality {
        public static int Z1 = SPHERE_Z_SAMPLES = 16;
        public static int Z2 = SPHERE_RADIAL_SAMPLES = 16;
        public static boolean Z9 = ENABLE_PLANETS = false;
        public static int Z3 = PLANET_PLANAR_QUADS_PER_PATCH = 4;
        public static boolean Z8 = PLANET_FAR_FILTER_ENABLED = true;
        public static int Z5 = SCREEN_WIDTH = 640;
        public static int Z6 = SCREEN_HEIGHT = 400;
        public static boolean Z7 = ENABLE_LEM_BLINKING = false;
    }    
}

