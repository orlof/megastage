package org.megastage.components.gfx;
    
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import org.megastage.client.JME3Material;

public class FloppyDriveGeometry extends ItemGeometryComponent {

    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createBlackBox());
    }

    private Spatial createBlackBox() {
        Geometry box = new Geometry("dcpu", new Box(0.5f, 0.5f, 0.5f));
        JME3Material.setUnshadedMaterial(box, ColorRGBA.Black);
        return box;
    }
}
