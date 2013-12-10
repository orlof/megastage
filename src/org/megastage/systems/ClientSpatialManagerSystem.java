/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.systems;

import com.artemis.Entity;
import com.artemis.systems.VoidEntitySystem;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.blocks.Block_Wood;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.LightNode;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.plugins.AWTLoader;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.Callable;
import jmeplanet.FractalDataSource;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.components.client.ClientRaster;
import org.megastage.components.server.MonitorGeometry;
import org.megastage.components.server.PlanetGeometry;
import org.megastage.components.server.ShipGeometry;
import org.megastage.components.server.SunGeometry;
import org.megastage.components.server.VoidGeometry;
import org.megastage.util.ClientGlobals;

/**
 *
 * @author Teppo
 */
public class ClientSpatialManagerSystem extends VoidEntitySystem {

    private final SimpleApplication app;
    private final AssetManager assetManager;
    private ClientEntityManagerSystem cems;
    private HashMap<Integer, Node> nodes = new HashMap<>();
    
    public ClientSpatialManagerSystem(SimpleApplication app) {
        this.app = app;
        assetManager = app.getAssetManager();
    }

    @Override
    protected void initialize() {
        cems = world.getSystem(ClientEntityManagerSystem.class);
    }

    private Node getNode(Entity entity) {
        int id = entity.getId();
        Node node = nodes.get(id);
 
        if(node == null) {
            node = new Node(entity.toString());
            nodes.put(id, node);
        }
        
        return node;
    }
    
    public void changeShip(final Entity shipEntity) {
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                leaveShip();
                enterShip(shipEntity);
                return null;
            }
        });
    }
    
    public void bindTo(final Entity parentEntity, final Entity childEntity) {
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                Node parentNode = getNode(parentEntity);
                Node childNode = getNode(childEntity);
                parentNode.attachChild(childNode);
                Log.info("Attach " + parentNode.getName() + " <- " + childNode.getName());
                return null;
            }
        });
    }
    
    private Node createUserNode(Entity entity) {
        // DON'T use for system nodes
        Node node = getNode(entity);
 
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        return node;
     }
    
    public void setupSunLikeBody(final Entity entity, final SunGeometry data) {
        Sphere mesh = new Sphere(
                ClientGlobals.gfxQuality.SPHERE_Z_SAMPLES,
                ClientGlobals.gfxQuality.SPHERE_RADIAL_SAMPLES, 
                data.radius);
        
        Geometry geom = new Geometry(entity.toString(), mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        ColorRGBA colorRGBA = new ColorRGBA();
        colorRGBA.fromIntRGBA(data.color);
        mat.setColor("Color", colorRGBA);
        geom.setMaterial(mat);

        final Node node = createUserNode(entity);
        node.attachChild(geom);

        final PointLight light = new PointLight();
        light.setColor(colorRGBA);
        light.setRadius(data.lightRadius);

        LightNode lightNode = new LightNode(entity.toString() + " Sun Light", light);
        node.attachChild(lightNode);

        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                ClientGlobals.rootNode.addLight(light);
                ClientGlobals.sysMovNode.attachChild(node);
                return null;
            }
        };
        app.enqueue(callable);
    }

    public void setupPlanetLikeBody(Entity entity, PlanetGeometry data) {
        // Add planet
        final Node node = createUserNode(entity);
        node.attachChild(createPlanet(data));

        PlanetAppState appState = app.getStateManager().getState(PlanetAppState.class);
        if(appState != null) appState.addPlanet((Planet) node.getChild(0));

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                if(node.getParent() == null) {
                    ClientGlobals.sysMovNode.attachChild(node);
                }
                return null;
            }
        });
    }
    
    public void setupVoidNode(Entity entity, VoidGeometry data) {
        // Add planet
        final Node node = createUserNode(entity);
    }
    
    public void setupShip(Entity entity, ShipGeometry data) {
        int chunkSize = data.size / 16 + 1;
        
        BlockTerrainControl shipBlockControl = new BlockTerrainControl(ClientGlobals.cubesSettings, new Vector3Int(chunkSize, chunkSize, chunkSize));

        shipBlockControl.setBlockArea(new Vector3Int(0,0,0), new Vector3Int(data.size, 1, data.size), Block_Wood.class);

        Node shipNode = new Node("main");
        shipNode.addControl(shipBlockControl);
        //shipNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        final Node node = createUserNode(entity);
        node.attachChild(shipNode);
        
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                if(node.getParent() == null) {
                    ClientGlobals.sysMovNode.attachChild(node);
                }
                return null;
            }
        });
    }

    public void setupMonitor(final Entity entity, final MonitorGeometry data) {
        Geometry geom = new Geometry(entity.toString(), new Quad(8, 6, true));
        
        BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
        Image img2 = new AWTLoader().load(img, false);
        ImageRaster raster = ImageRaster.create(img2);
        
        Texture2D tex = new Texture2D(img2);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        geom.setMaterial(mat);
        
        final Node node = createUserNode(entity);
        node.attachChild(geom);

        ClientRaster rasterComponent = cems.getComponent(entity, ClientRaster.class);
        rasterComponent.raster = raster;
    }

    private Planet createPlanet(PlanetGeometry data) {
        Log.info(data.toString());
        if(data.generator.equalsIgnoreCase("Earth")) {
            FractalDataSource planetDataSource = new FractalDataSource(4);
            planetDataSource.setHeightScale(data.radius / 100f);
            return Utility.createEarthLikePlanet(assetManager, data.radius, null, planetDataSource);
        } else if(data.generator.equalsIgnoreCase("Moon")) {
            FractalDataSource planetDataSource = new FractalDataSource(4);
            planetDataSource.setHeightScale(data.radius / 20f);
            return Utility.createMoonLikePlanet(assetManager, data.radius, planetDataSource);
        } else if(data.generator.equalsIgnoreCase("Water")) {
            return Utility.createWaterPlanet(assetManager, data.radius, null);
        } 

        throw new RuntimeException("Unknown planet generator: " + data.generator);
    }

    @Override
    protected void processSystem() {
    }

    private void leaveShip() {
        Entity shipEntity = ClientGlobals.shipEntity;
        
        if(shipEntity != null) {
            Log.debug(shipEntity.toString());

            ClientGlobals.shipEntity = null;

            Node shipNode = getNode(shipEntity);
            ClientGlobals.sysMovNode.attachChild(shipNode);
        }
    }
    
    private void enterShip(Entity shipEntity) {
        Log.debug(shipEntity.toString());

        ClientGlobals.shipEntity = shipEntity;

        Node shipNode = getNode(shipEntity);
        ClientGlobals.fixedNode.attachChild(shipNode);
    }

    public void setupPlayer(Entity entity) {
        ClientGlobals.playerNode.addControl(new PositionControl(entity));
        ClientGlobals.playerNode.addControl(new RotationControl(entity));
    }
}
