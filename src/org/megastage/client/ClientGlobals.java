package org.megastage.client;

import org.megastage.util.Log;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.ui.Picture;
import java.util.HashMap;
import org.megastage.protocol.UserCommand;
import org.megastage.systems.client.ClientNetworkSystem;
import org.megastage.util.ID;

public class ClientGlobals {
    public static ClientNetworkSystem network;
    
    public static int playerEntity;
    public static int baseEntity;
    
    public static long timeDiff;
    public static long syncTime;

    public static Node rootNode;
    public static Node playerNode;
    public static final Node backgroundNode = new Node("background_node");
    public static final Node globalRotationNode = new Node("global_rotation_node");

    public static GraphicsSettings gfxSettings = GraphicsSettings.JOKE;

    public static final UserCommand userCommand = new UserCommand();
    public static Main app;
    public static CommandHandler cmdHandler;
    public static String serverHost = "localhost";
    public static Picture crosshair;
    public static String player;
    public static String[] bootroms;
    public static String[] floppies;

    public static final HashMap<Class<?>, AppState> appStates = new HashMap<>(); 

    public static Camera cam;
    public static CameraNode camNode;

    static {
        appStates.put(ECSState.class, new ECSState());
        appStates.put(MainMenuState.class, new MainMenuState());
        appStates.put(DCPUMenuState.class, new DCPUMenuState());
    }
    
    public static void setAppStates(Class... enabledAppStates) {
        AppStateManager mgr = app.getStateManager();
        for(AppState appState: appStates.values()) {
            
            if(isInstance(appState, enabledAppStates)) {
                Log.info(appState.getClass().getSimpleName() + " active");
                mgr.attach(appState);
                appState.setEnabled(true);
            } else {
                Log.info(appState.getClass().getSimpleName() + " passive");
                mgr.detach(appState);
                appState.setEnabled(false);
            }
        }
    }
    
    private static boolean isInstance(AppState appState, Class[] types) {
        for(Class type: types) {
            if(type.isInstance(appState)) {
                return true;
            }
        }
        return false;
    }
    
    public static void setBase(int eid) {
        Log.info(ID.get(eid));

        unsetBase();
        
        EntityNode shipNode = SpatialManager.getOrCreateNode(eid);
        ClientGlobals.rootNode.attachChild(shipNode);

        ClientGlobals.baseEntity = eid;
    }

    private static void unsetBase() {
        if(ClientGlobals.baseEntity > 0) {
            EntityNode shipNode = SpatialManager.getOrCreateNode(ClientGlobals.baseEntity);
            ClientGlobals.globalRotationNode.attachChild(shipNode);
        
            ClientGlobals.baseEntity = 0;
        }
    }

    public static void setPlayer(int eid) {
        Log.info(ID.get(eid));

        unsetPlayer();
        
        EntityNode node = SpatialManager.getOrCreateNode(eid);
        node.setCullHint(Spatial.CullHint.Always);
        Node head = (Node) node.offset.getChild("head");
        if(head != null) {
            head.attachChild(ClientGlobals.camNode);
        }
        ClientGlobals.playerEntity = eid;
    }

    private static void unsetPlayer() {
        if(ClientGlobals.playerEntity > 0) {
            EntityNode node = SpatialManager.getOrCreateNode(ClientGlobals.playerEntity);
            node.setCullHint(Spatial.CullHint.Inherit);
            ClientGlobals.camNode.removeFromParent();
            ClientGlobals.playerEntity = 0;
        }
    }
}

