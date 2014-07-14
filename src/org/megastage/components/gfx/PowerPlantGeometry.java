package org.megastage.components.gfx;
    
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import org.megastage.client.JME3Electricity;
import org.megastage.client.JME3Material;

public class PowerPlantGeometry extends ItemGeometryComponent {
    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createBase());
        node.attachChild(createChamber());
    }

    private Spatial createChamber() {
        final Node node = new Node("chamber");

        Geometry cylinder = new Geometry("Reactor core", new Cylinder(16, 16, 0.45f, 0.9f, true));
        node.attachChild(cylinder);
        node.setLocalTranslation(0, 0.1f, 0);
        node.setLocalRotation(new Quaternion().fromAngles((float) (-Math.PI / 2.0), 0, 0));
        JME3Material.setLightingMaterial(node, ColorRGBA.White);
        
        JME3Electricity.ELECTRICITY1_2.electrify(node);
        
        return node;
    }
}
