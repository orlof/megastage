package org.megastage.client;

import com.esotericsoftware.minlog.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.client.controls.SystemControl;
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
        settings.setResolution(1024, 768);
        Main app = new Main();
        
        app.setSettings(settings);
        app.showSettings = false;
        app.start();
    }
    private PlanetAppState planetAppState;
    private ArtemisState artemisAppState;
    
    @Override
    public void simpleInitApp() {
        Node systemNode = new Node("system");
        // systemNode.setLocalTranslation(0f, 0f, -100000f);
        systemNode.addControl(new SystemControl());
        rootNode.attachChild(systemNode);

        // Add sky
        Node sceneNode = new Node("Scene");
        sceneNode.attachChild(Utility.createSkyBox(assetManager, "Textures/blue-glow-1024.dds"));
        systemNode.attachChild(sceneNode);

        // Add planet app state
        planetAppState = new PlanetAppState(systemNode);
        stateManager.attach(planetAppState);

        // Add ECS app state
        artemisAppState = new ArtemisState();
        stateManager.attach(artemisAppState);
        
        SpectatorModeInputManager in = new SpectatorModeInputManager(this);
        in.init();
        
        //inputManager.addRawInputListener(new DCPURawInputListener(artemisAppState));

    }

    @Override
    public void simpleUpdate(float tpf) {
        Globals.time = System.currentTimeMillis() + Globals.timeDiff;

        Log.info("CAM" + cam.getLocation().toString());
        
        // slow camera down as we approach a planet
        Planet planet = planetAppState.getNearestPlanet();
        if (planet != null && planet.getPlanetToCamera() != null) {
            this.getFlyByCamera().setMoveSpeed(
                    FastMath.clamp(planet.getDistanceToCamera(), 100, 100000));
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
