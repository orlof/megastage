package org.megastage.client;

import com.esotericsoftware.minlog.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import jmeplanet.PlanetAppState;
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
        settings.setResolution(1024,768);
        Main app = new Main();
        
        app.setSettings(settings);
        app.showSettings = false;
        app.start();
    }
    private PlanetAppState planetAppState;
    private ArtemisState artemisAppState;
    
    @Override
    public void simpleInitApp() {
        getFlyByCamera().setMoveSpeed(50000);
        
        Node systemNode = new Node("system");
        systemNode.setLocalTranslation(0f, 0f, -210000f);
        rootNode.attachChild(systemNode);

        /** Must add a light to make the lit object visible! */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-.1f, 0f, -1f));
        sun.setColor(new ColorRGBA(0.75f, 0.75f, 0.75f, 1.0f));      
        systemNode.addLight(sun);
        
        // Add sky
        //Node sceneNode = new Node("Scene");
        //sceneNode.attachChild(Utility.createSkyBox(assetManager, "Textures/blue-glow-1024.dds"));
        //systemNode.attachChild(sceneNode);

        // Add planet app state
        planetAppState = new PlanetAppState(systemNode, sun);
        stateManager.attach(planetAppState);

        // Add ECS app state
        artemisAppState = new ArtemisState();
        stateManager.attach(artemisAppState);
        
        //inputManager.addRawInputListener(new DCPURawInputListener(artemisAppState));

        // Add planet
        //FractalDataSource planetDataSource = new FractalDataSource(4);
        //planetDataSource.setHeightScale(900f);
        //Planet planet = Utility.createEarthLikePlanet(assetManager, 63710.0f, null, planetDataSource);
        //planetAppState.addPlanet(planet);
        //systemNode.attachChild(planet);

    }

    @Override
    public void simpleUpdate(float tpf) {
        Globals.time = System.currentTimeMillis() + Globals.timeDiff;
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
