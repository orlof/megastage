package org.megastage.client;

import com.jme3.app.SimpleApplication;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DomeTestApp extends SimpleApplication {

    public static void main(String[] args) {
        DomeTestApp app = new DomeTestApp();
        app.start();
    }
    private Node node;
    private Geometry geom2;

    @Override
    public void simpleInitApp() {
        Material unshaded = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Material lighting = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        lighting.setBoolean("UseMaterialColors",true);
        lighting.setColor("Diffuse", ColorRGBA.Blue);

        // setup sphere
        node = new Node();
        rootNode.attachChild(node);

        // setup dome
        geom2 = new Geometry("Dome", new Dome(new Vector3f(0,0,0), 16, 16, 1, false));
        geom2.setLocalTranslation(3, 0, 0);
        geom2.setMaterial(unshaded);
        rootNode.attachChild(geom2);

    }

    float t;
    
    @Override
    public void simpleUpdate(float tpf) {
        t += tpf;
        if(t > 2) {
            node.attachChild(geom2);
        }        
    }


}
