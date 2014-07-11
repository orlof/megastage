package org.megastage.client;

import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
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
    
    public static int playerEntity;
    public static int playerParentEntity;
    
    public static long timeDiff;

    public static Node rootNode;
    public static final Node backgroundNode = new Node("background_node");
    public static final Node globalRotationNode = new Node("global_rotation_node");
    public static final Node playerParentNode = new Node("player_parent_node");
    public static final Node playerNode = new Node("player_node");

    public static GraphicsSettings gfxSettings = GraphicsSettings.JOKE;

    public static final double SCALE = 1;
    public static final UserCommand userCommand = new UserCommand();
    public static Main app;
    public static CommandHandler cmdHandler;
    public static SpatialManager spatialManager;
    public static ECSState ecs;
    public static String serverHost = "localhost";
    public static Camera cam;
    public static Picture crosshair;
    public static String player;
    public static DCPUMenuState dcpuMenuState;
    public static String[] bootroms;
    public static String[] floppies;
}

