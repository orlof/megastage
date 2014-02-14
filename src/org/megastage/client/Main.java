package org.megastage.client;

import com.cubes.CubesSettings;
import com.cubes.test.CubesTestAssets;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.audio.AudioNode;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.client.controls.SystemPositionControl;
import org.megastage.client.controls.SystemRotationControl;
import org.megastage.util.LogFormat;
import org.megastage.util.Time;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        if(args.length > 0) {
            ClientGlobals.serverHost = args[0];
        }
        
        Log.setLogger(new LogFormat());
        Log.set(Log.LEVEL_INFO);

        AppSettings settings = new AppSettings(true);
        settings.setResolution(ClientGlobals.gfxSettings.SCREEN_WIDTH, ClientGlobals.gfxSettings.SCREEN_HEIGHT);
        Main app = new Main();

        ClientGlobals.app = app;
        
        app.setSettings(settings);
        app.showSettings = false;
        app.start();
    }

    private PlanetAppState planetAppState;
    
    public Main() {
        super((AppState) null);
    }
    
    @Override
    public void simpleInitApp() {
        SoundManager.init(assetManager);

        setPauseOnLostFocus(false);
        
        ClientGlobals.cmdHandler = new CommandHandler();
        ClientGlobals.cmdHandler.registerWithInput(inputManager);

        ClientGlobals.spatialManager = new SpatialManager(this);
        
        CameraNode camNode = new CameraNode("main", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        ClientGlobals.playerNode.attachChild(camNode);
        camNode.setLocalTranslation(0, 1.0f, 0);
        ClientGlobals.cam = cam;
        
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
        //planetAppState.setShadowsEnabled(ClientGlobals.gfxSettings.PLANET_SHADOWS_ENABLED);
        stateManager.attach(planetAppState);

        // Add ECS app state
        ClientGlobals.artemis = new ArtemisState();
        stateManager.attach(ClientGlobals.artemis);
        
        ClientGlobals.cubesSettings = new CubesSettings(this);
        ClientGlobals.cubesSettings.setBlockMaterial(CubesTestAssets.getSettings(this).getBlockMaterial());
        ClientGlobals.cubesSettings.setBlockSize(1);
        CubesTestAssets.registerBlocks();

        createCrosshair();
        
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.DarkGray);
        rootNode.addLight(ambient);
}

    @Override
    public void simpleUpdate(float tpf) {
        Time.value = System.currentTimeMillis() + ClientGlobals.timeDiff;
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void createCrosshair() {
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/red_crosshair.png", true);
        //pic.setWidth(settings.getWidth()/4);
        //pic.setHeight(settings.getHeight()/4);
        pic.setWidth(100);
        pic.setHeight(100);
        pic.setPosition(settings.getWidth()/2-50, settings.getHeight()/2-50);
        guiNode.attachChild(pic);
    }
    
}
