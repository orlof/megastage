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
import org.megastage.util.Globals;

public class Test extends SimpleApplication {
    
    static class A {
        public static int q=1;
        public int r() {
            return r1();
        }
        
        public int r1() {
            return 1;
        }
        
        public void f() {
            System.out.println(getClass());
        }
    }

    static class B extends A {
        B() {
            q=2;
        }
        public int r1() {
            return 2;
        }
    }
    
    public static void main(String[] args) {
        A a = new B();
        System.out.println(A.q);
        System.out.println(B.q);
        
//        calc(1,1,-1, "right up forward"); // ruf
//        //calc(1,-1,-1, "right down forward"); // rdf
//        calc(1,1,1, "right up back"); // rub
//        //calc(1,-1,1, "right down back"); // rdb
//        calc(-1,1,-1, "left up forward"); // ruf
//        //calc(-1,-1,-1, "left down forward"); // rdf
//        calc(-1,1,1, "left up back"); // rub
//        //calc(-1,-1,1, "left down back"); // rdb
    }

    public static void calc(double x, double y, double z, String name) {
        System.out.println(name);
        //pitch(x, y, z);
        yaw(x, y, z);
    }
    
    public static void pitch(double x, double y, double z) {
        System.out.println(" pitch: [" + x + ", " + y + ", " + z + "] " + Math.toDegrees(Math.atan2(y, Math.sqrt(x*x + z*z))));
    }
    
    public static void yaw(double x, double y, double z) {
        System.out.println(" yaw:   [" + x + ", " + y + ", " + z + "] " + Math.toDegrees(Math.atan2(x, -z)));
    }
    
    Node node = new Node();
    float flickerNess = 0.1f;
    float d = 10e20f;
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

        for (int i = 0; i < 100; i++) {
            Geometry geom = new Geometry("Twinkly Star " + i, q);
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.randomColor());
            geom.setMaterial(mat2);
            //geom.setLocalTranslation(nextRandomFloat() * d, nextRandomFloat() * d, nextRandomFloat() * d);
            geom.setLocalTranslation(0, 0, (float) (-1000+(i/10.0)));
            node.attachChild(geom);
        }
        rootNode.attachChild(node);
        
        final long start = System.currentTimeMillis();
        
        cam.setLocation(new Vector3f(0, 0, 00000));
        
        node.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                //System.out.println("controlUpdate " + System.currentTimeMillis());
                //if(System.currentTimeMillis() > start + 5000) spatial.removeFromParent();
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