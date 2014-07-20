package org.megastage.components.gfx;
    
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import org.megastage.client.JME3Material;
import org.megastage.client.JME3Electricity;

public class BatteryGeometry extends ItemGeometryComponent {

    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createBase());
        node.attachChild(createChamber());
    }

    private Spatial createChamber() {
        Node node = new Node("chamber");

        Geometry cylinder = new Geometry("Battery core", new Cylinder(6, 6, 0.45f, 0.9f, true));
        node.attachChild(cylinder);
        node.setLocalTranslation(0, 0.1f, 0);
        node.setLocalRotation(new Quaternion().fromAngles((float) (-Math.PI / 2.0), 0, 0));
        node.setMaterial(JME3Material.getLightingMaterial(ColorRGBA.Yellow));
        
        JME3Electricity.ELECTRICITY3_LINE1.electrify(node);
        
        return node;
    }
}
