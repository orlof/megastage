package org.megastage.util;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.cubes.*;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.*;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.Random;
import org.megastage.client.CubesManager;

public class CollisionTesterNew2 extends SimpleApplication {

    public static final int SIZE = 1;
    public static final int CHUNK_SIZE = 32;
    public static final boolean RANDOM = false;
    private Node terrainNode;

    public static void main(String[] args) {
        CollisionTesterNew2 app = new CollisionTesterNew2();
        app.start();
    }
    private Ship ship;
    private BlockTerrainControl ctrl;

    public CollisionTesterNew2() {
        showSettings = false;
        settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(400);
    }

    @Override
    public void simpleInitApp() {
        CubesSettings settings = new CubesSettings(this);
        settings.setBlockSize(1f);
        settings.setDefaultBlockMaterial("Textures/terrain.png");
        settings.setChunkSizeX(CHUNK_SIZE);
        settings.setChunkSizeY(CHUNK_SIZE);
        settings.setChunkSizeZ(CHUNK_SIZE);
        CubesTestAssets.registerBlocks();

        int numChunks = SIZE / CHUNK_SIZE;
        if (SIZE % CHUNK_SIZE > 0) {
            numChunks++;
        }

        Log.info("numChunks: %d", numChunks);

        CubesManager.init(this);
        
        ctrl = new BlockTerrainControl(settings, new Vector3Int(numChunks, numChunks, numChunks));

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
                        ctrl.setBlock(new Vector3Int(x, y, z), Block_Wood.class);
                        count++;
                    }
                }
            }
        }
        Log.info("Block count: " + count);
        Log.info("Center: %s", ship.getCenterOfMass());

        shipNode = new Node();
        rootNode.attachChild(shipNode);

        terrainNode = new Node();
        terrainNode.setLocalTranslation(-SIZE / 2.0f, -SIZE / 2.0f, -SIZE / 2.0f);
        terrainNode.addControl(ctrl);

        shipNode.attachChild(terrainNode);
        //shipNode.setLocalRotation(new Quaternion().fromAngles(FastMath.QUARTER_PI, 0, 0));

        cam.setLocation(new Vector3f(0, 0, SIZE * 2.0f));
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10);

        inputManager.addMapping("pick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(analogListener, "pick");
         
        createCrosshair();
    }
    Node shipNode;
    float tt;

    private void createCrosshair() {
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/red_crosshair.png", true);
        pic.setWidth(100);
        pic.setHeight(100);
        pic.setPosition(settings.getWidth() / 2 - 50, settings.getHeight() / 2 - 50);
        guiNode.attachChild(pic);
    }
    Vector3Int prevCollision;

    @Override
    public void simpleUpdate(float tpf) {
    }

    private CollisionResults getRayCastingResults(Node node) {
        Vector3f origin = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.0f);
        Vector3f direction = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        return results;
    }

    private Vector3Int getCurrentPointedBlockLocation(boolean getNeighborLocation) {
        CollisionResults results = getRayCastingResults(terrainNode);
        if (results.size() > 0) {
            Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint();
            return BlockNavigator.getPointedBlockLocation(ctrl, collisionContactPoint, getNeighborLocation);
        }
        return null;
    }

    public void initGeometry() {
        int size = ship.getSize();

        terrainNode.removeControl(BlockTerrainControl.class);
        ctrl = CubesManager.getControl(size);
        terrainNode.addControl(ctrl);

        // convert block map to Cubes control
        for(int x = 0; x <= size; x++) {
            for(int y = 0; y <= size; y++) {
                for(int z = 0; z <= size; z++) {
                    char c = ship.getBlock(x, y, z);
                    Class<? extends Block> block = CubesManager.getBlock(c);
                    if(block != null) {
                        ctrl.setBlock(x, y, z, block);
                    }
                }
            }
        }
    }
    
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            if (name.equals("pick")) {
                long start = System.currentTimeMillis();
                Vector3Int col = getCurrentPointedBlockLocation(true);
                long end = System.currentTimeMillis() - start;

                if (end > 1) {
                    Log.info("Collision time: %d", end);
                }

                if (col != null) {
                    int mv = ship.majorVersion;
                    Log.info("before: %s, %d", col, mv);

                    ship.setBlock(col, '#');
                    
                    Log.info("after: %s, %d", col, ship.majorVersion);

                    if(mv == ship.majorVersion) {
                        ctrl.setBlock(col, Block_Wood.class);
                    } else {
                        initGeometry();
                    }
                }
            }
        }
    };
}