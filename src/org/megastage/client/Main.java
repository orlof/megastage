package org.megastage.client;

import com.cubes.test.CubesTestAssets;
import org.megastage.util.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.TextureCubeMap;
import com.jme3.ui.Picture;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.Styles;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.megastage.client.controls.BackgroundRotationControl;
import org.megastage.client.controls.GlobalRotationControl;
import org.megastage.util.CmdLineParser;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        CmdLineParser cmd = new CmdLineParser(args);
        Log.set(cmd.getInteger("--log-level", Log.LEVEL_INFO));

        ClientGlobals.serverHost = cmd.getString("--server", "srv.megastage.org");
        ClientGlobals.player = cmd.getString("--player", "");
        
        ClientGlobals.gfxSettings = GraphicsSettings.valueOf(cmd.getString("--gfx", "HIGH"));
        
        ClientGlobals.app = new Main();        
        ClientGlobals.app.setSettings(getAppSettings());
        ClientGlobals.app.showSettings = ClientGlobals.gfxSettings.SHOW_SETTINGS;
        ClientGlobals.app.start();
    }

    public static AppSettings getAppSettings() {
        AppSettings settings = new AppSettings(true);

        try {
            BufferedImage[] icons = new BufferedImage[] {
                ImageIO.read(Main.class.getResource("icon128.png")),
                ImageIO.read(Main.class.getResource("icon64.png")),
                ImageIO.read(Main.class.getResource("icon16.png"))
            };
            settings.setIcons(icons);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        settings.setSettingsDialogImage("org/megastage/flash.jpg");
        settings.setTitle("Megastage");
        settings.setFullscreen(ClientGlobals.gfxSettings.FULL_SCREEN);
        settings.setResolution(ClientGlobals.gfxSettings.SCREEN_WIDTH, ClientGlobals.gfxSettings.SCREEN_HEIGHT);

        return settings;
    }
    
    public Main() {
        super((AppState) null);
    }
    
    @Override
    public void simpleInitApp() {
        ClientGlobals.inputManager = inputManager;
        
        SoundManager.init(assetManager);
        ExplosionNode.initialize(assetManager);

        setPauseOnLostFocus(false);
        
        initializeSystemNodes();
        initializeBackground();
        initializeCamera();
        initializeCubes();
        initializeCrosshair();
        
        // Add planet app state
        //planetAppState = new PlanetAppState(null);
        //planetAppState.setShadowsEnabled(ClientGlobals.gfxSettings.PLANET_SHADOWS_ENABLED);
        //stateManager.attach(planetAppState);

        // Add ECS app state
        GuiGlobals.initialize(this);
        Styles styles = GuiGlobals.getInstance().getStyles();
        LemurStyles.initializeStyles(styles);

        ClientGlobals.setAppStates(MainMenuState.class);
    }

    @Override
    public void simpleUpdate(float tpf) {
//        Time.value = System.currentTimeMillis() + ClientGlobals.timeDiff;
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void initializeCrosshair() {
        ClientGlobals.crosshair = new Picture("HUD Picture");
        ClientGlobals.crosshair.setImage(assetManager, "Textures/crosshairs.png", true);
        //pic.setWidth(settings.getWidth()/4);
        //pic.setHeight(settings.getHeight()/4);
        ClientGlobals.crosshair.setWidth(100);
        ClientGlobals.crosshair.setHeight(100);
        ClientGlobals.crosshair.setPosition(settings.getWidth()/2-50, settings.getHeight()/2-50);
        ClientGlobals.crosshair.setCullHint(Spatial.CullHint.Always);
        guiNode.attachChild(ClientGlobals.crosshair);
    }

    public CollisionResults getRayCastingResults(Node node) {
        Vector3f origin = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.0f);
        Vector3f direction = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        return results;
    }

    private void initializeSystemNodes() {
        ClientGlobals.rootNode = rootNode;

        ClientGlobals.globalRotationNode.addControl(new GlobalRotationControl());
        ClientGlobals.rootNode.attachChild(ClientGlobals.globalRotationNode);
    }
    
    private void initializeBackground() {
        Mesh sphere = new Sphere(10, 10, 10000f);
        sphere.setStatic();
        Geometry sky = new Geometry("SkyBox", sphere);
        sky.setQueueBucket(RenderQueue.Bucket.Sky);
        sky.setCullHint(Spatial.CullHint.Never);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        sky.setModelBound(new BoundingSphere(Float.POSITIVE_INFINITY, Vector3f.ZERO));

        Image cube = assetManager.loadTexture("Textures/blue-glow-1024.dds").getImage();
        TextureCubeMap cubemap = new TextureCubeMap(cube);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Sky.j3md");
        mat.setBoolean("SphereMap", false);
        mat.setTexture("Texture", cubemap);
        mat.setVector3("NormalScale", Vector3f.UNIT_XYZ);
        sky.setMaterial(mat);
        
        ClientGlobals.backgroundNode.attachChild(sky);
        ClientGlobals.rootNode.attachChild(ClientGlobals.backgroundNode);
        
        ClientGlobals.backgroundNode.addControl(new BackgroundRotationControl());
    }

    private void initializeCamera() {
        ClientGlobals.cam = cam;
        
        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setFrustumPerspective(45f, aspect, 0.3f, 40000f);

        ClientGlobals.camNode = new CameraNode("main_camera", cam);
        ClientGlobals.camNode.setControlDir(ControlDirection.SpatialToCamera);
        ClientGlobals.camNode.setLocalTranslation(0, 0.0f, -0.3f);
    }

    private void initializeCubes() {
        CubesManager.init(this);
        CubesTestAssets.registerBlocks();
    }
}
