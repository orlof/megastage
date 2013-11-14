/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.systems;

import com.artemis.Entity;
import com.artemis.systems.VoidEntitySystem;
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
import java.util.concurrent.Callable;
import jmeplanet.FractalDataSource;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.components.client.ClientRaster;
import org.megastage.components.client.ClientSpatial;
import org.megastage.components.server.MonitorGeometry;
import org.megastage.components.server.PlanetGeometry;
import org.megastage.components.server.SunGeometry;

/**
 *
 * @author Teppo
 */
public class ClientSpatialManagerSystem extends VoidEntitySystem {

    private final SimpleApplication app;

    private final AssetManager assetManager;
    private final PlanetAppState planetAppState;
    private final Node systemNode;
    private ClientEntityManagerSystem cems;
    
    public ClientSpatialManagerSystem(SimpleApplication app) {
        this.app = app;
        assetManager = app.getAssetManager();
        systemNode = (Node) app.getRootNode().getChild("system");
        planetAppState = app.getStateManager().getState(PlanetAppState.class);
    }

    @Override
    protected void initialize() {
        cems = world.getSystem(ClientEntityManagerSystem.class);
    }
    
    public void setupSunLikeBody(final Entity entity, final SunGeometry data) {
        Log.info("setupSunLikeBody");

        Sphere mesh = new Sphere(32, 32, data.radius);

        Geometry geom = new Geometry(entity.toString(), mesh);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        ColorRGBA colorRGBA = new ColorRGBA();
        colorRGBA.fromIntRGBA(data.color);
        mat.setColor("Color", colorRGBA);
        geom.setMaterial(mat);

        final Node node = new Node(entity.toString());
        node.attachChild(geom);

        final PointLight light = new PointLight();
        light.setColor(colorRGBA);
        light.setRadius(data.lightRadius);

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                app.getRootNode().addLight(light);
        
                LightNode lightNode = new LightNode(entity.toString() + " Sun Light", light);
                node.attachChild(lightNode);
                
                systemNode.attachChild(node);
                app.getStateManager().getState(PlanetAppState.class).addShadow(light);

                ClientSpatial cs = cems.getComponent(entity, ClientSpatial.class);
                cs.setNode(node);

                return null;
            }
        });
        
    }

    private Planet createPlanet(PlanetGeometry data) {
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

        return null;
    }
    
    public void setupPlanetLikeBody(final Entity entity, PlanetGeometry data) {
        Log.info("setupPlanetLikeBody");

        // Add planet
        final Planet planet = createPlanet(data);
        
        if(planet == null) {
            Log.error("Unknown planet generator: " + data.generator);
            return;
        }

        ClientSpatial cs = cems.getComponent(entity, ClientSpatial.class);
        cs.setNode(planet);

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                planetAppState.addPlanet(planet);
                systemNode.attachChild(planet);
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

        ClientSpatial cs = cems.getComponent(entity, ClientSpatial.class);
        cs.setNode(node);
        
        ClientRaster rasterComponent = cems.getComponent(entity, ClientRaster.class);
        rasterComponent.raster = raster;
    }

    @Override
    protected void processSystem() {
    }



}
