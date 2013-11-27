/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.systems;

import com.artemis.Entity;
import com.artemis.systems.VoidEntitySystem;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.Block_Wood;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
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
import java.util.concurrent.Callable;
import jmeplanet.FractalDataSource;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.components.client.ClientRaster;
import org.megastage.components.client.ClientSpatial;
import org.megastage.components.server.MonitorGeometry;
import org.megastage.components.server.PlanetGeometry;
import org.megastage.components.server.ShipGeometry;
import org.megastage.components.server.SunGeometry;

/**
 *
 * @author Teppo
 */
public class ClientSpatialManagerSystem extends VoidEntitySystem {

    private final SimpleApplication app;

    private final AssetManager assetManager;
    public final Node systemNode;
    private ClientEntityManagerSystem cems;
    
    public ClientSpatialManagerSystem(SimpleApplication app) {
        this.app = app;
        assetManager = app.getAssetManager();
        systemNode = (Node) app.getRootNode().getChild("system");
    }

    @Override
    protected void initialize() {
        cems = world.getSystem(ClientEntityManagerSystem.class);
    }

    private ClientSpatial addSpatialComponent(Entity entity, Node node) {
        node.setName(entity.toString());
 
        ClientSpatial cs = cems.getComponent(entity, ClientSpatial.class);
        cs.node = node;

        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));
        
        return cs;
    }
    
    public void setupSunLikeBody(final Entity entity, final SunGeometry data) {
        Log.info("setupSunLikeBody");
        
        final ClientSpatial cs = addSpatialComponent(entity, new Node());

        Sphere mesh = new Sphere(32, 32, data.radius);
        Geometry geom = new Geometry(entity.toString(), mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        ColorRGBA colorRGBA = new ColorRGBA();
        colorRGBA.fromIntRGBA(data.color);
        mat.setColor("Color", colorRGBA);
        geom.setMaterial(mat);

        cs.node.attachChild(geom);

        final PointLight light = new PointLight();
        light.setColor(colorRGBA);
        light.setRadius(data.lightRadius);

        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                app.getRootNode().addLight(light);
        
                LightNode lightNode = new LightNode(entity.toString() + " Sun Light", light);
                cs.node.attachChild(lightNode);

                systemNode.attachChild(cs.node);

                PlanetAppState appState = app.getStateManager().getState(PlanetAppState.class);
                if(appState != null) appState.addShadow(light);
                
                return null;
            }
        };
        app.enqueue(callable);
    }

    private Planet createPlanet(PlanetGeometry data) {
        Log.info("" + data.toString());
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
    
    public void setupPlanetLikeBody(Entity entity, PlanetGeometry data) {
        //Entity parent = cems.get(data.center);
                
        //final ClientSpatial parentSpatial = cems.getComponent(parent, ClientSpatial.class);
        
        // Add planet
        Node n = new Node(entity.toString());
        n.attachChild(createPlanet(data));
        final ClientSpatial cs = addSpatialComponent(entity, n);

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                systemNode.attachChild(cs.node);
                PlanetAppState appState = app.getStateManager().getState(PlanetAppState.class);
                if(appState != null) appState.addPlanet((Planet) cs.node.getChild(0));
                return null;
            }
        });
        
    }
    
    public void setupShip(Entity entity, ShipGeometry data) {
        Vector3Int size = new Vector3Int(data.size, data.size, data.size);
        
        //This is your ship, it is a whole /block world and offers methods to modify it
        //for simplicity all ships are rendered as floor plate only
        BlockTerrainControl shipBlockControl = new BlockTerrainControl(CubesTestAssets.getSettings(app), size);
 
        shipBlockControl.setBlockArea(new Vector3Int(0,0,0), size, Block_Wood.class);
        
        Node shipNode = new Node(entity.toString());
        shipNode.addControl(shipBlockControl);
        shipNode.setLocalTranslation(
                -(int) (1.5 * size.getX()),
                -(int) (1.5 * size.getY()),
                -(int) (1.5 * size.getZ()));
        shipNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        final ClientSpatial cs = addSpatialComponent(entity, shipNode);

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX " + cs.toString());
                systemNode.attachChild(cs.node);
                return null;
            }
        });
     }

    public void setupMonitor(Entity entity, MonitorGeometry data) {
        Log.info("setupMonitor");

        Geometry geom = new Geometry(entity.toString(), new Quad(3,2, true));
        
        BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
        Image img2 = new AWTLoader().load(img, false);
        ImageRaster raster = ImageRaster.create(img2);
        
        Texture2D tex = new Texture2D(img2);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        geom.setMaterial(mat);
        
        Node node = new Node(entity.toString());
        node.attachChild(geom);

        ClientSpatial cs = addSpatialComponent(entity, new Node());
        
        ClientRaster rasterComponent = cems.getComponent(entity, ClientRaster.class);
        rasterComponent.raster = raster;
    }

    @Override
    protected void processSystem() {
    }

}
