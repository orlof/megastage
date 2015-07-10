package org.megastage.util;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.cubes.*;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import org.megastage.client.CubesManager;
import org.megastage.server.Hit;
import org.megastage.server.ShipStructureHit;

public class CollisionTesterNew2 extends SimpleApplication {

    public static final int SIZE = 1;

    public static void main(String[] args) {
        CollisionTesterNew2 app = new CollisionTesterNew2();
        app.start();
    }

    private Ship ship;
    private Node shipNode;
    private Node terrainNode;
    private BlockTerrainControl ctrl;

    public CollisionTesterNew2() {
        showSettings = false;
        settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(400);
    }

    @Override
    public void simpleInitApp() {
        CubesManager.init(this);
        ctrl = CubesManager.getControl(1);
        ctrl.setBlock(0, 0, 0, CubesManager.getBlock('#'));

        ship = new Ship(1);
        ship.setBlock(new Vector3Int(0, 0, 0), '#');

        shipNode = new Node();
        //shipNode.setLocalTranslation(0, 1, 0);
        rootNode.attachChild(shipNode);

        terrainNode = new Node();
        terrainNode.setLocalTranslation(-SIZE / 2.0f, -SIZE / 2.0f, -SIZE / 2.0f);
        terrainNode.addControl(ctrl);

        shipNode.attachChild(terrainNode);

        cam.setLocation(new Vector3f(0, 0, 10));
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10);

        inputManager.addMapping("pick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("rot", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "pick");
        inputManager.addListener(actionListener, "rot");
         
        createCrosshair();
    }

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
        if(rotate) {
            shipNode.rotate(tpf, tpf, 0);
        }
    }

    public void initGeometry() {
        int size = ship.getSize();

        terrainNode.removeControl(BlockTerrainControl.class);
        ctrl = CubesManager.getControl(size);
        terrainNode.addControl(ctrl);

        // convert block componentName to Cubes control
        for(int x = 0; x < size; x++) {
            for(int y = 0; y < size; y++) {
                for(int z = 0; z < size; z++) {
                    char c = ship.getBlock(x, y, z);
                    Class<? extends Block> block = CubesManager.getBlock(c);
                    if(block != null) {
                        ctrl.setBlock(x, y, z, block);
                    }
                }
            }
        }
    }

    boolean rotate = false;
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("pick") && !isPressed) {
                Hit hit = ship.getHit(cam.getLocation().clone(), cam.getDirection().mult(50), shipNode.getWorldTranslation().clone(), shipNode.getWorldRotation().clone(), true);
                Log.info("%s", hit);

                if (hit instanceof ShipStructureHit) {
                    Vector3f com = ship.getCenterOfMass();
                    int majorVersion = ship.majorVersion;
                    ship.setBlock(((ShipStructureHit) hit).block, '#');
                    Log.info("MajorVersion: %d -> %d", majorVersion, ship.majorVersion);
                    
                    terrainNode.setLocalTranslation(ship.getCenterOfMass().negate());
                    shipNode.move(shipNode.getWorldRotation().multLocal(ship.getRelocation()));

                    if(majorVersion == ship.majorVersion) {
                        ctrl.setBlock(((ShipStructureHit) hit).block, CubesManager.getBlock('#'));
                    } else {
                        initGeometry();
                    }
                }
            }
            if (name.equals("rot")) {
                rotate = isPressed;
            }
        }
    };
}