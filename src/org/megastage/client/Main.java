package org.megastage.client;

import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import jmeplanet.FractalDataSource;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.PlanetSimpleTest;
import jmeplanet.test.Utility;
import org.megastage.components.ClientSpatial;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.systems.ClientSpatialManagerSystem;
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
        Log.info(cam.getLocation().toString());
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
