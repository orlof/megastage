package org.megastage.components.gfx;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import org.megastage.client.ClientGlobals;
import org.megastage.client.EntityNode;
import org.megastage.client.SpatialManager;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.ecs.ReplicatedComponent;
    
public abstract class CelestialGeometryComponent extends ReplicatedComponent {
    @Override
    public void receive(int eid) {
        super.receive(eid);

        EntityNode node = SpatialManager.getOrCreateNode(eid);
        
        node.addControl(new PositionControl(eid));
        node.addControl(new RotationControl(eid));

        initGeometry(node.offset, eid);

        ClientGlobals.globalRotationNode.attachChild(node);
    }

    protected abstract void initGeometry(Node node, int eid);
    
    @Override
    public void delete(int eid) {
    }

    protected Geometry createSphere(float radius) {
        Sphere mesh = new Sphere(
                ClientGlobals.gfxSettings.SPHERE_Z_SAMPLES,
                ClientGlobals.gfxSettings.SPHERE_RADIAL_SAMPLES, 
                radius);
        
        return new Geometry("celestial sphere", mesh);
    }
}
