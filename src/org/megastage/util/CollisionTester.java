package org.megastage.util;
 
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.cubes.*;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.*;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import java.util.Random;
 
public class CollisionTester extends SimpleApplication{
 
    public static void main(String[] args){
        CollisionTester app = new CollisionTester();
        app.start();
    }
 
    public CollisionTester(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Cubes Demo - Tutorial");
    }
 
    @Override
    public void simpleInitApp(){
        CubesSettings settings = new CubesSettings(this);
        settings.setDefaultBlockMaterial("Textures/terrain.png");
        settings.setBlockSize(1);
        settings.setChunkSizeX(8);
        settings.setChunkSizeY(8);
        settings.setChunkSizeZ(8);
        CubesTestAssets.registerBlocks();
 
        //This is your terrain, it contains the whole
        //block world and offers methods to modify it
        BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(1, 1, 1));

        Random rnd = new Random();
        Cube3dMap map = new Cube3dMap();
        for(int x=0; x < 8; x++) {
            for(int y=0; y < 8; y++) {
                for(int z=0; z < 8; z++) {
                    if(rnd.nextInt(5) == 0) {
                        map.set(x, y, z, '#');
                        blockTerrain.setBlock(new Vector3Int(x, y, z), Block_Wood.class); 
                    }
                }
            }
        }
        
        //The terrain is a jME-Control, you can add it
        //to a node of the scenegraph to display it
        Node terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
 
        cam.setLocation(new Vector3f(-10, 10, 16));
        cam.lookAtDirection(new Vector3f(1, -0.56f, -1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(50);
    }

    float t;
    
    @Override
    public void simpleUpdate(float tpf) {
        t += tpf;
        if(t > 2) {
            Vector3f origin = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.0f);
            Vector3f direction = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.3f);
            CubeCollisionDetector ccd = new CubeCollisionDetector(
                    cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.0f),
                    Vector3d.ZERO);
            System.out.println(getCurrentPointedBlockLocation(rootNode, false));
            System.out.println();
            t = 0;
        }        
    }

    public Vector3Int getCurrentPointedBlockLocation(Node terrainNode, boolean getNeighborLocation){
        CollisionResults results = getRayCastingResults(terrainNode);
        if(results.size() > 0) {
            Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint();
            collisionContactPoint.subtractLocal(terrainNode.getLocalTranslation());
            BlockTerrainControl ctrl = terrainNode.getControl(BlockTerrainControl.class);
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
        return results;
    }

    
}
