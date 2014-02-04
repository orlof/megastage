package org.megastage.client;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Quad;
import com.jme3.util.BufferUtils;
import java.util.Random;

public class Test extends SimpleApplication {

    public static void main(String[] args) {
        Test app = new Test();
        app.start();
    }
    Node[] layers = new Node[3];
    float flickerNess = 0.1f;
    float d = 10e20f;
    // planetary system diameter
    
    @Override
    public void simpleUpdate(float tpf) {
        //layers[nextRandomInt(0, 2)].setLocalTranslation(nextRandomFloat() * flickerNess, nextRandomFloat() * flickerNess, nextRandomFloat() * flickerNess);
    }

    @Override
    public void simpleInitApp() {
        cam.setFrustumFar(2f*d);

        Vector3f [] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(0,0,0);
        Mesh q = new Mesh();

        //q.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));

        //Quad q = new Quad(1f, 1f, false);
        q.setMode(Mesh.Mode.Points);
        q.setPointSize(10f);
        q.updateBound();
        q.setStatic();

        layers[0] = new Node("Layer1");
        layers[1] = new Node("Layer2");
        layers[2] = new Node("Layer3");

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        
        Geometry geom = new Geometry("Twinkly Star Mother", q);
        geom.setMaterial(mat);
        geom.setLocalTranslation(0, 0, -1000);
        layers[0].attachChild(geom);

        for (int i = 0; i < 100; i++) {
            geom = new Geometry("Twinkly Star " + i, q);
            geom.setMaterial(mat);
            int level = nextRandomInt(0, 2);
            geom.setLocalTranslation(nextRandomFloat() * d, nextRandomFloat() * d, nextRandomFloat() * d);
            layers[level].attachChild(geom);
        }
        rootNode.attachChild(layers[0]);
        rootNode.attachChild(layers[1]);
        rootNode.attachChild(layers[2]);
    }

    Random random = new Random();
    private float nextRandomFloat() {
        return 2f * random.nextFloat() - 1f;
    }

    private int nextRandomInt(int min, int max) {
        return random.nextInt(max-min) + min;
    }
}