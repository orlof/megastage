package org.megastage.components.gfx;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import org.jdom2.Element;
import org.megastage.client.JME3Material;
import org.megastage.client.controls.ThermalLaserControl;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;
    
public class ThermalLaserGeometry extends ItemGeometryComponent {
    public float length;
    public Vector3f attackVector;
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        length = getFloatValue(element, "length", 3.0f);
        attackVector = getVector3f(element, "attack_vector", new Vector3f(0.0f, 0.0f, -1.0f)); 
        return null;
    }

    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createGun());
        node.attachChild(createBeam(eid));
    }

    private Spatial createGun() {
        Geometry geom = new Geometry("weapon", new Cylinder(16, 16, 0.5f, 0.3f, length, true, false));
        geom.setLocalTranslation(0, 0, -length/2f + 0.5f);
        JME3Material.setLightingMaterial(geom, ColorRGBA.Gray);
        return geom;
    }

    private Spatial createBeam(int eid) {
        Cylinder cyl = new Cylinder(6, 6, 0.2f, 0.2f, 100, true, false);

        Geometry geom = new Geometry("beam", cyl);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent); // Remenber to set the queue bucket to transparent for the spatial
        geom.setLocalTranslation(0, 0, -100/2f);
        
        Material mat = JME3Material.getGlowingMaterial(new ColorRGBA(1f, 1f, 0f, 0.8f));
        // TRANSPARENT?
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        // ---
        geom.setMaterial(mat);

        geom.addControl(new ThermalLaserControl(eid));
        
        return geom;
    }
}
