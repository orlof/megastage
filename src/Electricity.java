import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 * @author cvlad
 */
public class Electricity extends SimpleApplication {

    public static void main(String[] args) {
        Electricity app = new Electricity();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        electrify("Materials/Electricity/electricity1.j3m", 0);
        electrify("Materials/Electricity/electricity1_2.j3m", 2f);
        electrify("Materials/Electricity/electricity2.j3m", 4);
        electrify("Materials/Electricity/electricity2_2.j3m", 6);
        electrify("Materials/Electricity/electricity3_line1.j3m", 8);
        electrify("Materials/Electricity/electricity3_line2.j3m", 10);
        electrify("Materials/Electricity/electricity3_line3.j3m", 12);
        electrify("Materials/Electricity/electricity4.j3m", 14);
        electrify("Materials/Electricity/electricity5_2.j3m", 16);
        
        DirectionalLight light = new DirectionalLight();
        light.setColor(new ColorRGBA(0.6f,0.6f,0.6f,0.6f));
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.6f,0.6f,0.6f,0.6f));
        light.setDirection(new Vector3f(-1,-1,-1));
        rootNode.addLight(light);
        rootNode.addLight(ambient);
        
        flyCam.setMoveSpeed(10);
        viewPort.setBackgroundColor(ColorRGBA.Gray);   
    }

    void electrify(String materialName, float x) {
        Node man = new Node(materialName);
        man.attachChild(new Geometry(materialName, new Sphere(16, 16, 0.5f)));
        //man.attachChild(new Geometry(materialName, new Cylinder(16, 16, 0.45f, 0.9f, true)));
        //man.attachChild(new Geometry(materialName, new Dome(Vector3f.ZERO, 16, 16, 0.5f, false)));

        Material matMan = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        man.setMaterial(matMan);
        
        Material mat = assetManager.loadMaterial(materialName);
        
        for (Spatial child : ((Node)man).getChildren()){
            if (child instanceof Geometry){
                Geometry electricity = new Geometry("electrified_" + child.getName());
                electricity.setQueueBucket(Bucket.Transparent);
                electricity.setMesh(((Geometry)child).getMesh());
                electricity.setMaterial(mat);
                ((Node)man).attachChild(electricity);
            }
        }

        man.move(x, 0, 0);
        //man.setLocalRotation(new Quaternion().fromAngles((float) (-Math.PI / 2.0), 0, 0));
        rootNode.attachChild(man);
    }
    
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
