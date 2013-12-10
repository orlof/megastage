package org.megastage.client;

import com.cubes.CubesSettings;
import com.cubes.test.CubesTestAssets;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.client.controls.SystemPositionControl;
import org.megastage.client.controls.SystemRotationControl;
import org.megastage.util.ClientGlobals;
import org.megastage.util.Globals;
import org.megastage.util.LogFormat;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private final static String MAPPING_DCPU = "DCPU";
    
    public static void main(String[] args) {
        Log.setLogger(new LogFormat());
        Log.set(Log.LEVEL_INFO);

        AppSettings settings = new AppSettings(true);
        settings.setResolution(ClientGlobals.gfxQuality.SCREEN_WIDTH, ClientGlobals.gfxQuality.SCREEN_HEIGHT);
        Main app = new Main();
        
        app.setSettings(settings);
        app.showSettings = false;
        app.start();
    }

    private PlanetAppState planetAppState;
    private ArtemisState artemisAppState;
    //private SpectatorCamera spectatorCam;
    
    public Main() {
        super();
        //super( new StatsAppState(), new DebugKeysAppState()/*, new SpectatorCamAppState() */);
    }
    
    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(25);
        
        ClientGlobals.rootNode = rootNode;
        //cam.setLocation(new Vector3f(16, 6, 60));

        ClientGlobals.fixedNode.attachChild(ClientGlobals.playerNode);
        ClientGlobals.rootNode.attachChild(ClientGlobals.fixedNode);
        
        ClientGlobals.sysRotNode.addControl(new SystemRotationControl());
        ClientGlobals.sysMovNode.addControl(new SystemPositionControl());
        
        rootNode.attachChild(ClientGlobals.sysRotNode);

        ClientGlobals.sysRotNode.attachChild(ClientGlobals.sysMovNode);

        // Add sky
        ClientGlobals.sceneNode = new Node("Scene");
        ClientGlobals.sceneNode.attachChild(Utility.createSkyBox(assetManager, "Textures/blue-glow-1024.dds"));
        ClientGlobals.rootNode.attachChild(ClientGlobals.sceneNode);

        // Add planet app state
        planetAppState = new PlanetAppState(null);
        //planetAppState.setShadowsEnabled(ClientGlobals.gfxQuality.PLANET_SHADOWS_ENABLED);
        stateManager.attach(planetAppState);

        // Add ECS app state
        artemisAppState = new ArtemisState();
        stateManager.attach(artemisAppState);
        
        //spectatorCam = new SpectatorCamera(cam);
        //stateManager.getState(SpectatorCamAppState.class).setCamera(spectatorCam);

        //SpectatorModeInputManager in = new SpectatorModeInputManager(this);
        //in.init();
        
        //inputManager.addRawInputListener(new DCPURawInputListener(artemisAppState));

        ClientGlobals.cubesSettings = new CubesSettings(this);
        ClientGlobals.cubesSettings.setBlockMaterial(CubesTestAssets.getSettings(this).getBlockMaterial());
        ClientGlobals.cubesSettings.setBlockSize(2);
        CubesTestAssets.registerBlocks();
     }

    @Override
    public void simpleUpdate(float tpf) {
        Globals.time = System.currentTimeMillis() + ClientGlobals.timeDiff;

        Log.trace("Camera coords: " + cam.getLocation().toString());
        
        // slow camera down as we approach a planet
        Planet planet = planetAppState.getNearestPlanet();
        if (planet != null && planet.getPlanetToCamera() != null) {
            //this.spectatorCam.setMoveSpeed(
            //        FastMath.clamp(planet.getDistanceToCamera(), 100, 100000));
        } else {
            //this.spectatorCam.setMoveSpeed(100000);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
