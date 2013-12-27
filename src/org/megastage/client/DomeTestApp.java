package org.megastage.client;

import com.jme3.app.SimpleApplication;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;

public class DomeTestApp extends SimpleApplication {

    public static void main(String[] args) {
        DomeTestApp app = new DomeTestApp();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Material unshaded = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Material lighting = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        lighting.setBoolean("UseMaterialColors",true);
        lighting.setColor("Diffuse", ColorRGBA.Blue);

        // setup light
        Geometry geom = new Geometry("Light", new Sphere(16, 16, .1f));
        geom.setLocalTranslation(-3, 0, 0);
        geom.setMaterial(unshaded);
        rootNode.attachChild(geom);
        
        PointLight light = new PointLight();
        rootNode.addLight(light);

        // setup sphere
        geom = new Geometry("Sphere", new Cylinder(16, 16, 0.5f, 1, false));
        geom.setLocalTranslation(-3, 0, 0);
        geom.setMaterial(lighting);
        rootNode.attachChild(geom);

        // setup dome
        geom = new Geometry("Dome", new Dome(new Vector3f(0,0,0), 16, 16, 1, false));
        geom.setLocalTranslation(3, 0, 0);
        geom.setMaterial(lighting);
        rootNode.attachChild(geom);
    }
}
