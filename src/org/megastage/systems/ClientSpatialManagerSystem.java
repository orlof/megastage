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
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.plugins.AWTLoader;
import java.awt.image.BufferedImage;
import org.megastage.components.ClientRaster;
import org.megastage.components.ClientSpatial;
import org.megastage.components.ClientVideoMemory;
import org.megastage.components.Position;

/**
 *
 * @author Teppo
 */
public class ClientSpatialManagerSystem extends VoidEntitySystem {

    private AssetManager assetManager;
    private Node rootNode;
    
    public ClientSpatialManagerSystem(SimpleApplication app) {
        this.assetManager = app.getAssetManager();
        this.rootNode = app.getRootNode();
    }
    
    public void setupMonitor(Entity entity) {
        Log.info("setupMonitor");

        Geometry geom = new Geometry(entity.toString(), new Quad(3,2, true));
        
        BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
        Image img2 = new AWTLoader().load(img, false);
        ImageRaster raster = ImageRaster.create(img2);
        
        Texture2D tex = new Texture2D(img2);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        //mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        
        rootNode.attachChild(geom);
        
        ClientSpatial spatialComponent = new ClientSpatial();
        spatialComponent.geom = geom;
        entity.addComponent(spatialComponent);
        
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
            spatial.setLocalTranslation(position.getAsVector());
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {}
        
    }
}
