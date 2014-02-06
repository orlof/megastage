package org.megastage.client;

import com.artemis.Entity;
import com.cubes.CubesSettings;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import org.megastage.protocol.UserCommand;
import org.megastage.systems.client.ClientNetworkSystem;

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
    public static Camera cam;
    
}

