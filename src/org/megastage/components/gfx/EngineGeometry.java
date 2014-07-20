package org.megastage.components.gfx;
    
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import org.megastage.client.ClientGlobals;
import org.megastage.client.JME3Material;
import org.megastage.client.controls.EngineControl;

public class EngineGeometry extends ItemGeometryComponent {

    @Override
    protected void initGeometry(Node node, int eid) {
        node.attachChild(createEngine());
        node.attachChild(createAfterBurn(eid));
    }

    private Spatial createEngine() {
        Geometry geom = new Geometry("", new Cylinder(16, 16, 0.5f, 1.0f, true));
        JME3Material.setLightingMaterial(geom, ColorRGBA.Gray);
        return geom;
    }
    
    private Spatial createAfterBurn(int eid) {
        Node node = (Node) ClientGlobals.app.getAssetManager().loadModel("Scenes/testScene.j3o"); 
        ParticleEmitter emitter = (ParticleEmitter) node.getChild("Emitter");
        emitter.addControl(new EngineControl(eid));
        emitter.setEnabled(true);
        return node;
    }
}
