package org.megastage.client;

import com.cubes.BlockTerrainControl;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.Block_Brick;
import com.cubes.test.blocks.Block_Grass;
import com.cubes.test.blocks.Block_Stone;
import com.cubes.test.blocks.Block_Wood;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.client.controls.SystemPositionControl;
import org.megastage.client.controls.SystemRotationControl;
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
        Log.set(Log.LEVEL_DEBUG);

        AppSettings settings = new AppSettings(true);
        settings.setResolution(640, 400);
        Main app = new Main();
        
        app.setSettings(settings);
        app.showSettings = false;
        app.start();
    }

    private PlanetAppState planetAppState;
    private ArtemisState artemisAppState;
    private SpectatorCamera spectatorCam;
    
    public Main() {
        super( new StatsAppState(), new DebugKeysAppState(), new SpectatorCamAppState() );
    }
    
    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(0, 0, 0));

        Node systemRotNode = new Node("system_rot");
        systemRotNode.addControl(new SystemRotationControl());
        rootNode.attachChild(systemRotNode);
        
        Node systemNode = new Node("system");
        systemNode.addControl(new SystemPositionControl());
        systemRotNode.attachChild(systemNode);

        // Add sky
        Node sceneNode = new Node("Scene");
        sceneNode.attachChild(Utility.createSkyBox(assetManager, "Textures/blue-glow-1024.dds"));
        systemRotNode.attachChild(sceneNode);

        // Add planet app state
        planetAppState = new PlanetAppState(systemNode);
        planetAppState.setShadowsEnabled(false);
        stateManager.attach(planetAppState);

        // Add ECS app state
        artemisAppState = new ArtemisState();
        stateManager.attach(artemisAppState);
        
        spectatorCam = new SpectatorCamera(cam, artemisAppState);
        stateManager.getState(SpectatorCamAppState.class).setCamera(spectatorCam);

        //SpectatorModeInputManager in = new SpectatorModeInputManager(this);
        //in.init();
        
        //inputManager.addRawInputListener(new DCPURawInputListener(artemisAppState));

        CubesTestAssets.registerBlocks();
 
 
    }

    @Override
    public void simpleUpdate(float tpf) {
        Globals.time = System.currentTimeMillis() + Globals.timeDiff;

        Log.trace("Camera coords: " + cam.getLocation().toString());
        
        // slow camera down as we approach a planet
        Planet planet = planetAppState.getNearestPlanet();
        if (planet != null && planet.getPlanetToCamera() != null) {
            this.spectatorCam.setMoveSpeed(
                    FastMath.clamp(planet.getDistanceToCamera(), 100, 100000));
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
