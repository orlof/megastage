package org.megastage.client;

import com.cubes.CubesSettings;
import com.cubes.test.CubesTestAssets;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.system.AppSettings;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.client.controls.SystemPositionControl;
import org.megastage.client.controls.SystemRotationControl;
import org.megastage.util.ClientGlobals;
import org.megastage.util.LogFormat;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        if(args.length > 0) {
            ClientGlobals.serverHost = args[0];
        }
        
        Log.setLogger(new LogFormat());
        Log.set(Log.LEVEL_INFO);

        AppSettings settings = new AppSettings(true);
        settings.setResolution(ClientGlobals.gfxQuality.SCREEN_WIDTH, ClientGlobals.gfxQuality.SCREEN_HEIGHT);
        Main app = new Main();

        ClientGlobals.app = app;
        
        app.setSettings(settings);
        app.showSettings = false;
        app.start();
    }

    private PlanetAppState planetAppState;
    
    public Main() {
        super(null);
    }
    
    @Override
    public void simpleInitApp() {
        ClientGlobals.cmdHandler = new CommandHandler();
        ClientGlobals.cmdHandler.registerWithInput(inputManager);

        ClientGlobals.spatialManager = new SpatialManager(this);
        
        CameraNode camNode = new CameraNode("Camera 1", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        ClientGlobals.playerNode.attachChild(camNode);
        
        ClientGlobals.rootNode = rootNode;
        cam.setLocation(new Vector3f(16, 6, 60));

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
        ClientGlobals.artemis = new ArtemisState();
        stateManager.attach(ClientGlobals.artemis);
        
        ClientGlobals.cubesSettings = new CubesSettings(this);
        ClientGlobals.cubesSettings.setBlockMaterial(CubesTestAssets.getSettings(this).getBlockMaterial());
        ClientGlobals.cubesSettings.setBlockSize(2);
        CubesTestAssets.registerBlocks();
     }

    @Override
    public void simpleUpdate(float tpf) {
        ClientGlobals.time = System.currentTimeMillis() + ClientGlobals.timeDiff;

        Log.trace("Camera coords: " + cam.getLocation().toString());
        if(Log.TRACE) {
            float[] eulerAngles = cam.getRotation().toAngles(null);
            Log.trace("Camera(yaw="+(FastMath.RAD_TO_DEG * eulerAngles[0])+", roll="+(FastMath.RAD_TO_DEG * eulerAngles[1])+", pitch="+(FastMath.RAD_TO_DEG * eulerAngles[2])+")");
        }
        
        // slow camera down as we approach a planet
//        Planet planet = planetAppState.getNearestPlanet();
//        if (planet != null && planet.getPlanetToCamera() != null) {
//            this.spectatorCam.setMoveSpeed(
//                    FastMath.clamp(planet.getDistanceToCamera(), 100, 100000));
//        } else {
//            this.spectatorCam.setMoveSpeed(100000);
//        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
