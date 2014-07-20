package org.megastage.components.gfx;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import org.megastage.client.EntityNode;
import org.megastage.client.JME3Material;
import org.megastage.client.SpatialManager;
import org.megastage.client.controls.LocalPositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.ecs.ReplicatedComponent;
    
public abstract class ItemGeometryComponent extends ReplicatedComponent {
    @Override
    public void receive(int eid) {
        super.receive(eid);

        EntityNode node = SpatialManager.getOrCreateNode(eid);
        
        node.addControl(new LocalPositionControl(eid));
        node.addControl(new RotationControl(eid));

        initGeometry(node.offset, eid);
    }

    protected abstract void initGeometry(Node node, int eid);
    
    @Override
    public void delete(int eid) {
    }

    protected Spatial createBase() {
        Geometry geom = new Geometry("base", new Box(0.5f, 0.05f, 0.5f));
        JME3Material.setTexturedMaterial(geom, ColorRGBA.Gray, "rock09.jpg");
        geom.setLocalTranslation(0, -0.45F, 0);
        return geom;
    }
}
