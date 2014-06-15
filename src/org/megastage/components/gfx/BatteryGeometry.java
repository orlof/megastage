package org.megastage.components.gfx;
    
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import org.megastage.client.JME3Material;
import org.megastage.client.JME3Electricity;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;

public class BatteryGeometry extends GeometryComponent {
    public void setupBattery(int eid, BatteryGeometry aThis) {
        Node node = SpatialManager.getNode(eid);
        PositionControl positionControl = new  PositionControl(eid);
        RotationControl rotationControl = new  RotationControl(eid);

        final Geometry base = new Geometry("base", new Box(0.5f, 0.05f, 0.5f));
        base.setMaterial(JME3Material.getMaterial("rock09.jpg"));
        //base.setLocalTranslation(0, -0.45f, 0);
        base.setLocalTranslation(0, -0.45f, 0);

        final Node chamber = new Node("chamber");

        Geometry cylinder = new Geometry("Battery core", new Cylinder(6, 6, 0.45f, 0.9f, true));
        chamber.attachChild(cylinder);
        chamber.setLocalTranslation(0, 0.1f, 0);
        chamber.setLocalRotation(new Quaternion().fromAngles((float) (-Math.PI / 2.0), 0, 0));
        chamber.setMaterial(JME3Material.getLighting(ColorRGBA.Yellow));
        
        JME3Electricity.ELECTRICITY3_LINE1.electrify(chamber);

        node.addControl(positionControl);
        node.addControl(rotationControl);

        attach(node, base, true);
        attach(node, chamber, true);
    }
}
