package org.megastage.client;

import org.megastage.util.Log;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.HashMap;
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
    public static long syncTime;

    public static Node rootNode;
    public static final Node backgroundNode = new Node("background_node");
    public static final Node globalRotationNode = new Node("global_rotation_node");
    public static final Node playerNode = new Node("player_node");

    public static GraphicsSettings gfxSettings = GraphicsSettings.JOKE;

    public static final UserCommand userCommand = new UserCommand();
    public static Main app;
    public static CommandHandler cmdHandler;
    public static String serverHost = "localhost";
    public static Camera cam;
    public static Picture crosshair;
    public static String player;
    public static String[] bootroms;
    public static String[] floppies;

    public static final HashMap<Class<?>, AppState> appStates = new HashMap<>(); 

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
    
}

