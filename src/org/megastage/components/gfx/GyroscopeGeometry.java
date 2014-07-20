package org.megastage.components.gfx;
    
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import org.jdom2.Element;
import org.megastage.client.JME3Material;
import org.megastage.client.controls.GyroscopeControl;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class GyroscopeGeometry extends ItemGeometryComponent {
    public float angleX, angleY, angleZ;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        angleX = (float) Math.toRadians(getDoubleValue(element, "angle_x", 0.0));
        angleY = (float) Math.toRadians(getDoubleValue(element, "angle_y", 0.0));
        angleZ = (float) Math.toRadians(getDoubleValue(element, "angle_z", 0.0));
        
        return null;
    }

    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createBase());
        node.attachChild(createWheel(eid));
    }
    
    private Spatial createWheel(int eid) {
        Geometry wheel = new Geometry("wheel", new Cylinder(5, 5, 0.35f, 0.35f, 0.45f, true, false));
        wheel.setLocalRotation(new Quaternion().fromAngles(angleX, angleY, angleZ));
        JME3Material.setTexturedMaterial(wheel, ColorRGBA.Gray, "rock09.jpg");

        wheel.addControl(new GyroscopeControl(eid));
        
        return wheel;
    }
}
