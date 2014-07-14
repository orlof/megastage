package org.megastage.components.gfx;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import org.megastage.client.EntityNode;
import org.megastage.client.JME3Material;
import org.megastage.client.SpatialManager;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.ecs.ReplicatedComponent;
    
public abstract class ItemGeometryComponent extends ReplicatedComponent {
    @Override
    public void receive(int eid) {
        super.receive(eid);

        EntityNode node = SpatialManager.getOrCreateNode(eid);
        
        node.addControl(new PositionControl(eid));
        node.addControl(new RotationControl(eid));

        initGeometry(node.offset, eid);
    }

    protected abstract void initGeometry(Node node, int eid);
    
    @Override
    public void delete(int eid) {
    }

    protected Spatial createBase() {
        Geometry node = new Geometry("base", new Box(0.5F, 0.05F, 0.5F));
        JME3Material.setBasicMaterial(node, "rock09.jpg");
        node.setLocalTranslation(0, -0.45F, 0);
        return node;
    }
}
