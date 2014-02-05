package org.megastage.client;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.control.AbstractControl;
import com.jme3.util.BufferUtils;
import java.util.Random;

public class Test extends SimpleApplication {

    public static void main(String[] args) {
        Test app = new Test();
        app.start();
    }
    Node node = new Node();
    float flickerNess = 0.1f;
    float d = 10e2f;
    // planetary system diameter
    
    @Override
    public void simpleUpdate(float tpf) {
        
    }

    @Override
    public void simpleInitApp() {
        cam.setFrustumFar(2f*d);

        Vector3f [] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(0,0,0);
        Mesh q = new Mesh();

        q.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));

        //Quad q = new Quad(1f, 1f, false);
        q.setMode(Mesh.Mode.Points);
        q.setPointSize(10f);
        q.updateBound();
        q.setStatic();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        
        Geometry geom = new Geometry("Twinkly Star Mother", q);
        geom.setMaterial(mat);
        geom.setLocalTranslation(0, 0, -1000);
        node.attachChild(geom);

        for (int i = 0; i < 100; i++) {
            geom = new Geometry("Twinkly Star " + i, q);
            geom.setMaterial(mat);
            geom.setLocalTranslation(nextRandomFloat() * d, nextRandomFloat() * d, nextRandomFloat() * d);
            node.attachChild(geom);
        }
        rootNode.attachChild(node);
        
        final long start = System.currentTimeMillis();
        
        node.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                System.out.println("controlUpdate " + System.currentTimeMillis());
                if(System.currentTimeMillis() > start + 5000) spatial.removeFromParent();
                spatial.move(0, 0, 25*tpf);
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });
    }

    Random random = new Random();
    private float nextRandomFloat() {
        return 2f * random.nextFloat() - 1f;
    }

    private int nextRandomInt(int min, int max) {
        return random.nextInt(max-min) + min;
    }
}