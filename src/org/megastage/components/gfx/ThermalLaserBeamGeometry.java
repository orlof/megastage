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
    
public class ThermalLaserBeamGeometry extends ItemGeometryComponent {
    public Vector3f attackVector;
    
    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createBeam(eid));
    }

    private Spatial createBeam(int eid) {
        Cylinder cyl = new Cylinder(6, 6, 0.2f, 0.2f, 100, true, false);

        Geometry geom = new Geometry("beam", cyl);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);
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
