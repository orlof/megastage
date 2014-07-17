package org.megastage.util;
 
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.cubes.*;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.*;
import org.megastage.util.Log;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.Random;
 
public class CollisionTester extends SimpleApplication{
 
    public static void main(String[] args){
        CollisionTester app = new CollisionTester();
        app.start();
    }
    private Cube3dMap map;
    private BlockTerrainControl blockTerrain;
 
    public CollisionTester(){
        showSettings = false;
        settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(400);
    }
    
    public static final int BIG = 32;
    
    @Override
    public void simpleInitApp(){
        CubesSettings settings = new CubesSettings(this);
        settings.setBlockSize(1f);
        settings.setDefaultBlockMaterial("Textures/terrain.png");
        settings.setChunkSizeX(BIG);
        settings.setChunkSizeY(BIG);
        settings.setChunkSizeZ(BIG);
        CubesTestAssets.registerBlocks();
 
        blockTerrain = new BlockTerrainControl(settings, new Vector3Int(1, 1, 1));

        int c = 0;
        Random rnd = new Random();
        map = new Cube3dMap();
        for(int x=0; x < BIG; x++) {
            for(int y=0; y < BIG; y++) {
                for(int z=0; z < BIG; z++) {
                    if(rnd.nextInt(10) == 0) {
                        map.set(x, y, z, '#');
                        blockTerrain.setBlock(new Vector3Int(x, y, z), Block_Wood.class); 
                        c++;
                    }
                }
            }
        }
        Log.info("Block count: " + c);

        ship = new Node();
        rootNode.attachChild(ship);
        Node offset = new Node();
        ship.attachChild(offset);
        offset.setLocalTranslation(-BIG/2, -BIG/2, -BIG/2);
        
        Node terrainNode = new Node();
        terrainNode.addControl(blockTerrain);

        offset.attachChild(terrainNode);

        cam.setLocation(new Vector3f(BIG/2, BIG/2, 30));
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10);

        createCrosshair();        
    }

    Node ship;
    float tt;
    
    private void createCrosshair() {
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/red_crosshair.png", true);
        pic.setWidth(100);
        pic.setHeight(100);
        pic.setPosition(settings.getWidth()/2-50, settings.getHeight()/2-50);
        guiNode.attachChild(pic);
    }

    Vector3Int prevCollision;
        
    @Override
    public void simpleUpdate(float tpf) {
        //tt+=tpf;
        tt=0;
        Quaternion q = new Quaternion().fromAngles(tt/20, tt/15, 0);
        ship.setLocalRotation(q);

        Vector3Int collision = CubeCollisionDetector.getCollision(
                new Vector3f(0,0,0), new Quaternion(q), map, 
                new Vector3f(cam.getLocation()), 
                new Vector3f(cam.getDirection()));

        if(collision != prevCollision) {
            if(prevCollision != null) {
                blockTerrain.setBlock(prevCollision, Block_Wood.class);
            }

            prevCollision = collision;
            
            if(prevCollision != null) {
                blockTerrain.setBlock(prevCollision, Block_Water.class);
            }
        }
    }
}
