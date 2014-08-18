package org.megastage.util;
 
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.ui.Picture;
 
public class TestCubeCollision extends SimpleApplication{
    
    public static void main(String[] args){
        TestCubeCollision app = new TestCubeCollision();
        app.start();
    }
 
    public TestCubeCollision(){
        showSettings = false;
        settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(400);
    }

    Node node;
    Geometry geom;
    
    @Override
    public void simpleInitApp(){
//        DirectionalLight sun = new DirectionalLight();
//        sun.setColor(ColorRGBA.White);
//        sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
//        rootNode.addLight(sun);

        node = new Node("center");
        node.setLocalTranslation(2, 0, 0);
        Quaternion q = new Quaternion().fromAngles(0, FastMath.DEG_TO_RAD*90, 0);
        node.setLocalRotation(q);
        rootNode.attachChild(node);

        geom = new Geometry("Box", new Box(1,1,1));
        geom.setLocalTranslation(2, 0, 0);
        geom.setLocalRotation(q);
        Material mat = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        geom.setMaterial(mat);                   // set the cube's material
        node.attachChild(geom);
        
        cam.setLocation(new Vector3f(0, 0, 10));
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10);

        createCrosshair();        
    }

    private void createCrosshair() {
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/red_crosshair.png", true);
        pic.setWidth(100);
        pic.setHeight(100);
        pic.setPosition(settings.getWidth()/2-50, settings.getHeight()/2-50);
        guiNode.attachChild(pic);
    }

    @Override
    public void simpleUpdate(float tpf) {
        float[] ang = new float[3];
        node.getLocalRotation().toAngles(ang);
        Vector3f loc = new Vector3f(ang[0], ang[1], ang[2]).multLocal(FastMath.RAD_TO_DEG);
        node.getWorldRotation().toAngles(ang);
        Vector3f glob = new Vector3f(ang[0], ang[1], ang[2]).multLocal(FastMath.RAD_TO_DEG);

        Log.info("node: %s / %s", loc, glob);

        geom.getLocalRotation().toAngles(ang);
        loc = new Vector3f(ang[0], ang[1], ang[2]).multLocal(FastMath.RAD_TO_DEG);
        geom.getWorldRotation().toAngles(ang);
        glob = new Vector3f(ang[0], ang[1], ang[2]).multLocal(FastMath.RAD_TO_DEG);

        Log.info("geom: %s / %s", loc, glob);
    }
}
