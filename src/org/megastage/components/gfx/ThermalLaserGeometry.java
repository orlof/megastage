package org.megastage.components.gfx;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import org.jdom2.Element;
import org.megastage.client.JME3Material;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;
    
public class ThermalLaserGeometry extends ItemGeometryComponent {
    public float length;
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        length = getFloatValue(element, "length", 3.0f);

        ThermalLaserBeamGeometry tlbg = new ThermalLaserBeamGeometry();
        tlbg.attackVector = getVector3f(element, "attack_vector", new Vector3f(0.0f, 0.0f, -1.0f));

        return new BaseComponent[] { tlbg };
    }

    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createGun());
    }

    private Spatial createGun() {
        Geometry geom = new Geometry("weapon", new Cylinder(16, 16, 0.5f, 0.3f, length, true, false));
        geom.setLocalTranslation(0, 0, -length/2f + 0.5f);
        JME3Material.setLightingMaterial(geom, ColorRGBA.Gray);
        return geom;
    }
}
