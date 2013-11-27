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
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
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
import com.jme3.util.TangentBinormalGenerator;
import java.awt.image.BufferedImage;
import org.megastage.components.ClientRaster;
import org.megastage.components.ClientSpatial;
import org.megastage.components.ClientVideoMemory;
import org.megastage.components.Position;
import org.megastage.protocol.Network;

/**
 *
 * @author Teppo
 */
public class ClientSpatialManagerSystem extends VoidEntitySystem {

    private AssetManager assetManager;
    private Node rootNode;
    private Node worldNode;
    private Node shipNode;
    
    public ClientSpatialManagerSystem(SimpleApplication app) {
        assetManager = app.getAssetManager();
        rootNode = app.getRootNode();
        
        app.getFlyByCamera().setMoveSpeed(50000);
        
        //AmbientLight ambient = new AmbientLight();
        //ambient.setColor(ColorRGBA.White);
        //rootNode.addLight(ambient);
        
        /** Must add a light to make the lit object visible! */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,0).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        worldNode = new Node("world");
        // worldNode.setLocalTranslation(0, 0, -4000000f);
        worldNode.setLocalTranslation(0, 0, -210000f);
        rootNode.attachChild(worldNode);
        
        shipNode = new Node("ship");
        rootNode.attachChild(shipNode);
    }
    
   void setupSphere(Entity entity, Network.SpatialSphereData data) {
        Log.info("setupSphere");

        Sphere mesh = new Sphere(data.spatial.zSamples, data.spatial.radialSamples, data.spatial.radius);
        //Sphere mesh = new Sphere(data.spatial.zSamples, data.spatial.radialSamples, 1000000f);
        mesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres

        Geometry geom = new Geometry(entity.toString(), mesh);
        TangentBinormalGenerator.generate(mesh);
        
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
        mat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
        mat.setBoolean("UseMaterialColors",true);    
        mat.setColor("Diffuse",ColorRGBA.White);
        mat.setColor("Specular",ColorRGBA.White);
        mat.setFloat("Shininess", 64f);  // [0,128]
        geom.setMaterial(mat);
        
        //ColorRGBA colorRGBA = new ColorRGBA();
        //colorRGBA.fromIntRGBA(data.spatial.color);
        //mat.setColor("Color", colorRGBA);
        //geom.setMaterial(mat);
        
        worldNode.attachChild(geom);

        entity.addComponent(new ClientSpatial(geom));

        geom.addControl(new PositionControl(entity));
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
        
        shipNode.attachChild(geom);
        
        entity.addComponent(new ClientSpatial(geom));
        
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