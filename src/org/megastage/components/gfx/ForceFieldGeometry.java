package org.megastage.components.gfx;
    
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import org.megastage.client.JME3Electricity;
import org.megastage.client.JME3Material;
import org.megastage.client.controls.ForceFieldControl;

public class ForceFieldGeometry extends ItemGeometryComponent {

    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createBase());
        node.attachChild(createDome());
        node.attachChild(createShield(eid));
    }
     
    private Spatial createDome() {
        Node node = new Node("dome");

        Geometry cylinder = new Geometry("item", new Sphere(12, 12, 0.45f));
        node.attachChild(cylinder);
        node.setLocalTranslation(0, 0.05f, 0);

        JME3Material.setLightingMaterial(node, ColorRGBA.Black);
        JME3Electricity.ELECTRICITY3_LINE2.electrify(node);
        
        return node;
    }

    private Spatial createShield(int eid) {
        // Create spatial to be the shield
        Sphere sphere = new Sphere(30, 30, 15);
        Geometry geom = new Geometry("forceshield", sphere);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);
 
        // Create ForceShieldControl
        Material material = JME3Material.getBasicMaterial("ShaderBlow/MatDefs/ForceShield/ForceShield.j3md");
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.setFloat("MaxDistance", 1);

        ForceFieldControl forceFieldControl = new ForceFieldControl(eid, material);
        geom.addControl(forceFieldControl); // Add the control to the spatial
        forceFieldControl.setEffectSize(10f); // Set the effect size
        forceFieldControl.setColor(new ColorRGBA(0, 0, 1, 1)); // Set effect color
        forceFieldControl.setVisibility(0.1f); // Set shield visibility.
 
        // Set a texture to the shield
        forceFieldControl.setTexture(JME3Material.getTexture("Textures/fs_texture.png"));
        
        return geom;
    }

}
