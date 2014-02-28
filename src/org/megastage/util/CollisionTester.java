package org.megastage.util;
 
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.cubes.*;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.*;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.ui.Picture;
import java.util.Random;
 
public class CollisionTester extends SimpleApplication{
 
    public static void main(String[] args){
        CollisionTester app = new CollisionTester();
        app.start();
    }
    private Cube3dMap map;
    private BlockTerrainControl blockTerrain;
    private Geometry geom;
 
    public CollisionTester(){
        showSettings = false;
        settings = new AppSettings(true);
        settings.setWidth(320);
        settings.setHeight(200);
    }
    
 
    @Override
    public void simpleInitApp(){
        CubesSettings settings = new CubesSettings(this);
        settings.setBlockSize(1f);
        settings.setDefaultBlockMaterial("Textures/terrain.png");
        settings.setChunkSizeX(8);
        settings.setChunkSizeY(8);
        settings.setChunkSizeZ(8);
        CubesTestAssets.registerBlocks();
 
        blockTerrain = new BlockTerrainControl(settings, new Vector3Int(1, 1, 1));

        Random rnd = new Random();
        map = new Cube3dMap();
        for(int x=0; x < 8; x++) {
            for(int y=0; y < 8; y++) {
                for(int z=0; z < 8; z++) {
                    if(rnd.nextInt(10) == 0) {
                        map.set(x, y, z, '#');
                        blockTerrain.setBlock(new Vector3Int(x, y, z), Block_Wood.class); 
                    }
                }
            }
        }
        
        Node terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        terrainNode.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(terrainNode);

        cam.setLocation(new Vector3f(4, 4, 10));
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(50);

        Material unshaded = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        createCrosshair();
        
        // setup dome
        geom = new Geometry("Dome", new Sphere(16, 16, 0.5f));
        geom.setMaterial(unshaded);
        rootNode.attachChild(geom);
    }

    float t;
    
    private void createCrosshair() {
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/red_crosshair.png", true);
        //pic.setWidth(settings.getWidth()/4);
        //pic.setHeight(settings.getHeight()/4);
        pic.setWidth(100);
        pic.setHeight(100);
        pic.setPosition(settings.getWidth()/2-50, settings.getHeight()/2-50);
        guiNode.attachChild(pic);
    }

    @Override
    public void simpleUpdate(float tpf) {
        t += tpf;
        Vector3f cl = cam.getLocation();
        Vector3f cd = cam.getDirection();
        
        Vector3f pos = cl.add(cd.mult(20));
        
        geom.setLocalTranslation(pos);
        
        if(t > 2) {
//            Vector3f of = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.0f);
//            Vector3f df = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.3f);
//            
//            Vector3d od = new Vector3d(of.x, of.y, of.z);
//            Vector3d dd = new Vector3d(df.x, df.y, df.z);
            
            System.out.println(cam.getLocation());
            System.out.println(cam.getDirection());
            CubeCollisionDetector ccd = new CubeCollisionDetector(new Vector3d(cam.getLocation()), new Vector3d(cam.getDirection()));
            Vector3Int col2 = ccd.collision(new Vector3d(4,4,4), new Quaternion(1,0,0,0), map);
            
            System.out.println(col2);
            System.out.println();
//            System.out.println(getCurrentPointedBlockLocation(rootNode, false));
//            System.out.println();
            t = 0;
        }        
    }

    public Vector3Int getCurrentPointedBlockLocation(Node terrainNode, boolean getNeighborLocation){
        CollisionResults results = getRayCastingResults(terrainNode);
        if(results.size() > 0) {
            Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint();
            collisionContactPoint.subtractLocal(terrainNode.getLocalTranslation());
            System.out.println(collisionContactPoint);
            BlockTerrainControl ctrl = terrainNode.getControl(BlockTerrainControl.class);
            System.out.println(ctrl);
            return BlockNavigator.getPointedBlockLocation(ctrl, collisionContactPoint, getNeighborLocation);
        }
        return null;
    }

    public CollisionResults getRayCastingResults(Node node) {
        Vector3f origin = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.0f);
        Vector3f direction = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        System.out.println(results.toString());
        return results;
    }

    
}
