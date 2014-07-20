package org.megastage.components.gfx;
    
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Dome;
import org.megastage.client.JME3Material;
import org.megastage.client.controls.LookAtControl;

public class RadarGeometry extends ItemGeometryComponent {

    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createBase());
        node.attachChild(createAntenna(eid));
    }
    
    private Spatial createAntenna(int eid) {
        Node spinner = new Node("Antenna");
        
        Quaternion t = new Quaternion().fromAngles((float) (-Math.PI / 2.0), 0, 0);
        spinner.setLocalRotation(t);
        
        Geometry inside = new Geometry("inside", new Dome(Vector3f.ZERO, 12, 12, 0.38f, true));
        JME3Material.setLightingMaterial(inside, new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f));
        spinner.attachChild(inside);

        Geometry outside = new Geometry("outside", new Dome(Vector3f.ZERO, 12, 12, 0.39f, false));
        JME3Material.setLightingMaterial(outside, new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f));
        spinner.attachChild(outside);
        
        spinner.addControl(new LookAtControl(eid));
        return spinner;
    }
}
