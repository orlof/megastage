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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
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
import org.megastage.components.ClientRaster;
import org.megastage.components.ClientSpatial;
import org.megastage.components.OrbitalRotation;
import org.megastage.components.Position;
import org.megastage.protocol.Network;

/**
 *
 * @author Teppo
 */
public class ClientSpatialManagerSystem extends VoidEntitySystem {

    private final SimpleApplication app;

    private final AssetManager assetManager;
    private final PlanetAppState planetAppState;
    private final Node systemNode;
    
    public ClientSpatialManagerSystem(SimpleApplication app) {
        this.app = app;
        assetManager = app.getAssetManager();
        systemNode = (Node) app.getRootNode().getChild("system");
        planetAppState = app.getStateManager().getState(PlanetAppState.class);
    }
    
   void setupSunLikeBody(final Entity entity, final Network.SpatialSunData data) {
        Log.info("setupSunLikeBody");

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                Sphere mesh = new Sphere(32, 32, data.spatial.radius);

                Geometry geom = new Geometry(entity.toString(), mesh);

                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                //Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

                ColorRGBA colorRGBA = new ColorRGBA();
                //colorRGBA.fromIntRGBA(data.spatial.color);
                mat.setColor("Color", ColorRGBA.White);
                geom.setMaterial(mat);

                Node node = new Node(entity.toString());
                node.attachChild(geom);

                systemNode.attachChild(node);

                //AmbientLight al = new AmbientLight();
                //al.setColor(ColorRGBA.White.mult(1.3f));
                //node.addLight(al);

                entity.addComponent(new ClientSpatial(node));

                geom.addControl(new PositionControl(entity));
                return null;
            }
        });
        
    }

    void setupPlanetLikeBody(final Entity entity, final Network.SpatialPlanetData data) {
        Log.info("setupPlanetLikeBody");

        // Add planet
        FractalDataSource planetDataSource = new FractalDataSource(4);
        planetDataSource.setHeightScale(data.spatial.radius / 20f);
        final Planet planet = Utility.createEarthLikePlanet(assetManager, data.spatial.radius, null, planetDataSource);

        entity.addComponent(new ClientSpatial(planet));

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                planetAppState.addPlanet(planet);
                systemNode.attachChild(planet);

                planet.addControl(new PositionControl(entity));
//                planet.addControl(new OrbitalRotationControl(entity));
                return null;
            }
        });
        
    }
    
    public void setupMonitor(Entity entity, Network.SpatialMonitorData data) {
        Log.info("setupMonitor");

        Geometry geom = new Geometry(entity.toString(), new Quad(3,2, true));
        
        BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
        Image img2 = new AWTLoader().load(img, false);
        ImageRaster raster = ImageRaster.create(img2);
        
        Texture2D tex = new Texture2D(img2);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        geom.setMaterial(mat);
        
        //shipNode.attachChild(geom);
        
        entity.addComponent(new ClientSpatial(geom.getParent()));
        
        ClientRaster rasterComponent = new ClientRaster();
        rasterComponent.raster = raster;
        entity.addComponent(rasterComponent);
        
        geom.addControl(new PositionControl(entity));
    }

    @Override
    protected void processSystem() {
    }

    class PositionControl extends AbstractControl {
        private final Entity entity;
        public PositionControl(Entity entity) {
            this.entity = entity;
        }
        
        @Override
        protected void controlUpdate(float tpf) {
            Position position = entity.getComponent(Position.class);
//            if(position != null) {
                spatial.setLocalTranslation(position.getAsVector());
//            }
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {}
        
    }


}
