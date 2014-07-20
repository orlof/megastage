package org.megastage.components.gfx;
    
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Torus;
import org.megastage.client.JME3Material;
import org.megastage.client.controls.RandomSpinnerControl;

public class PPSGeometry extends ItemGeometryComponent {
    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createBase());
        node.attachChild(createSensor());
    }

    private Spatial createSensor() {
        Geometry spinner = new Geometry("pps sensor", new Torus(12, 12, 0.05f, 0.2f));
        JME3Material.setLightingMaterial(spinner, new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f));
        spinner.addControl(new RandomSpinnerControl());

        return spinner;
    }
}
