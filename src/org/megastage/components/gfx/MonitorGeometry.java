package org.megastage.components.gfx;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.plugins.AWTLoader;
import java.awt.image.BufferedImage;
import org.jdom2.Element;
import org.megastage.client.JME3Material;
import org.megastage.components.client.ClientRaster;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class MonitorGeometry extends ItemGeometryComponent {
    public float width, height;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        width = getFloatValue(element, "width", 3.0f);
        height = getFloatValue(element, "height", 2.0f);
        
        return null;
    }

    @Override
    protected void initGeometry(Node node, int eid) {
        node.setLocalTranslation(-0.5f, -0.5f, 0.0f);
        node.attachChild(createBox());
        node.attachChild(createPanel(eid));
    }

    private Spatial createPanel(int eid) {
        BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
        Image img2 = new AWTLoader().load(img, false);
        ImageRaster raster = ImageRaster.create(img2);
        
        Texture2D tex = new Texture2D(img2);
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.Trilinear);

        Material mat = JME3Material.getBasicMaterial("Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);

        Geometry geom = new Geometry("LEM panel", new Quad(width-0.2f, height-0.2f, true));
        geom.setMaterial(mat);
        geom.setLocalTranslation(0.1f, 0.1f, 0.5f);

        ClientRaster rasterComponent = World.INSTANCE.getOrCreateComponent(eid, CompType.ClientRaster, ClientRaster.class);
        rasterComponent.raster = raster;
        
        return geom;
    }
    
    private Spatial createBox() {
        Geometry geom = new Geometry("LEM box", new Box(width/2.0f, height/2.0f, 0.45f));
        JME3Material.setLightingMaterial(geom, ColorRGBA.Gray);
        geom.setLocalTranslation(width / 2.0f, height / 2.0f, 0.0f);
        return geom;
    }
}
