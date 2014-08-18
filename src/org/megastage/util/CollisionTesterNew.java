package org.megastage.util;
 
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.cubes.*;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.*;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.Random;
import org.megastage.server.Hit;
import org.megastage.server.NoHit;
import org.megastage.server.ShipStructureHit;
 
public class CollisionTesterNew extends SimpleApplication{
    
    public static final int SIZE = 64;
    public static final int CHUNK_SIZE = 64;
    public static final boolean RANDOM = true;
 
    public static void main(String[] args){
        CollisionTesterNew app = new CollisionTesterNew();
        app.start();
    }
    private Ship ship;
    private BlockTerrainControl blockTerrain;
 
    public CollisionTesterNew(){
        showSettings = false;
        settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(400);
    }
    
    @Override
    public void simpleInitApp(){
        CubesSettings settings = new CubesSettings(this);
        settings.setBlockSize(1f);
        settings.setDefaultBlockMaterial("Textures/terrain.png");
        settings.setChunkSizeX(CHUNK_SIZE);
        settings.setChunkSizeY(CHUNK_SIZE);
        settings.setChunkSizeZ(CHUNK_SIZE);
        CubesTestAssets.registerBlocks();

        int numChunks = SIZE / CHUNK_SIZE;
        if(SIZE % CHUNK_SIZE > 0) numChunks++;
        
        Log.info("numChunks: %d", numChunks);
        blockTerrain = new BlockTerrainControl(settings, new Vector3Int(numChunks, numChunks, numChunks));

        int count = 0;
        Random rnd = new Random();
        ship = new Ship(SIZE);
        Vector3Int c = new Vector3Int();
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                for (int z = 0; z < SIZE; z++) {
                    if (!RANDOM || rnd.nextInt(10) == 0) {
                        c.set(x, y, z);
                        ship.setBlock(c, '#');
                        blockTerrain.setBlock(new Vector3Int(x, y, z), Block_Wood.class);
                        count++;
                    }
                }
            }
        }
        Log.info("Block count: " + count);
        Log.info("Center: %s", ship.getCenterOfMass());

        shipNode = new Node();
        rootNode.attachChild(shipNode);
        
        Node terrainNode = new Node();
        terrainNode.setLocalTranslation(-SIZE / 2.0f, -SIZE / 2.0f, -SIZE / 2.0f);
        terrainNode.addControl(blockTerrain);

        shipNode.attachChild(terrainNode);
        //shipNode.setLocalRotation(new Quaternion().fromAngles(FastMath.QUARTER_PI, 0, 0));

        cam.setLocation(new Vector3f(0, 0, SIZE * 2.0f));
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10);

        createCrosshair();        
    }

    Node shipNode;
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
        //Log.info("Loc: %s", cam.getLocation());
        //Log.info("Dir: %s", cam.getDirection());
        //tt+=tpf;
        
        //shipNode.rotate(tpf / 10, tpf / 10, 0);

        long start = System.currentTimeMillis();
        Hit hit = ship.getHit(
                cam.getLocation().clone(), 
                cam.getDirection().mult(100.0f),
                Vector3f.ZERO.clone(), 
                shipNode.getLocalRotation().clone());
        long end = System.currentTimeMillis() - start;
        
        if(end > 1) {
            Log.info("Collision time: %d", end);
        }

        Vector3Int collision = (hit == NoHit.INSTANCE) ? null: ((ShipStructureHit) hit).block;

        if(collision == null) {
            if(prevCollision != null) {
                //Log.info("No collision");
                blockTerrain.setBlock(prevCollision, Block_Wood.class);
                prevCollision = null;
            }
        } else if(!collision.equals(prevCollision)) {
            //Log.info("%s", collision);

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
