package org.megastage.client;

import com.cubes.BlockNavigator;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import org.megastage.util.Log;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.megastage.components.Rotation;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.protocol.CharacterMode;

public class CommandHandler implements AnalogListener, ActionListener {

    public int mode = CharacterMode.NONE;
    private InputManager inputManager;
    private DCPURawInputListener dcpuListener = new DCPURawInputListener();

    public void registerWithInput(InputManager inputManager) {
        this.inputManager = inputManager;

        //inputManager.setCursorVisible(false);
        //initWalkMode();
    }

    public void unregisterInput() {

        if (inputManager == null) {
            return;
        }

        inputManager.clearMappings();
        inputManager.clearRawInputListeners();

        inputManager.setCursorVisible(false);
    }

    public void changeMode(int newMode) {
        if(newMode == mode) return;
        
        Log.info("Mode change " + mode + " -> " + newMode);
        
        switch(mode) {
            case CharacterMode.WALK:
                exitWalkMode();
                break;
            case CharacterMode.DCPU:
                exitDCPUMode();
                break;
            case CharacterMode.MENU:
                exitMenuMode();
                break;
        }

        switch(newMode) {
            case CharacterMode.WALK:
                initWalkMode();
                break;
            case CharacterMode.DCPU:
                initDCPUMode();
                break;
            case CharacterMode.MENU:
                initMenuMode();
                break;
        }
    }
    
    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (ClientGlobals.playerEntity == 0) {
            return;
        }

        switch (name) {
            case "WALK_LookLeft":
                lookLeft(value);
                break;
            case "WALK_LookRight":
                lookLeft(-value);
                break;
            case "WALK_LookUp":
                lookUp(value * (invertY ? -1 : 1));
                break;
            case "WALK_LookDown":
                lookUp(-value * (invertY ? -1 : 1));
                break;
            case "WALK_MoveForward":
                move(value, false);
                break;
            case "WALK_MoveBackward":
                move(-value, false);
                break;
            case "WALK_MoveLeft":
                move(value, true);
                break;
            case "WALK_MoveRight":
                move(-value, true);
                break;
            case "SHIP_MoveForward":
                moveShip(0, 0, -value);
                break;
            case "SHIP_MoveBackward":
                moveShip(0, 0, value);
                break;
            case "SHIP_MoveUp":
                moveShip(0, value, 0);
                break;
            case "SHIP_MoveDown":
                moveShip(0, -value, 0);
                break;
            case "SHIP_MoveLeft":
                moveShip(-value, 0, 0);
                break;
            case "SHIP_MoveRight":
                moveShip(value, 0, 0);
                break;
            case "SHIP_PitchUp":
                pitch(value);
                break;
            case "SHIP_PitchDown":
                pitch(-value);
                break;
            case "SHIP_RollCW":
                roll(-value);
                break;
            case "SHIP_RollCCW":
                roll(value);
                break;
            case "SHIP_YawLeft":
                yaw(value);
                break;
            case "SHIP_YawRight":
                yaw(-value);
                break;
        }
    }

    @Override
    public void onAction(String name, boolean value, float tpf) {
        if (!value) {
            switch (name) {
                case "WALK_InvertY":
                    invertY = !invertY;
                    break;
                case "ITEM_Pick":
                    pickItem(false);
                    break;
                case "ITEM_RightPick":
                    pickItem(true);
                    break;
                case "DCPU_Exit":
                    unpickItem();
                    break;
                case "MENU_Exit":
                    unpickItem();
                    break;
                case "GAME_TogglePointer":
                    InputManager inputManager = ClientGlobals.app.getInputManager(); 
                    inputManager.setCursorVisible(!inputManager.isCursorVisible());
                    break;
                case "GAME_Exit":
                    ClientGlobals.app.stop();
                    break;
            }
        }
    }
    protected float rotationSpeed = 1f;
    protected float walkSpeed = 3f;
    protected float shipSpeed = 100f;
    protected boolean invertY = true;

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    protected void lookUp(float value) {
        Rotation rot = (Rotation) World.INSTANCE.getComponent(ClientGlobals.playerEntity, CompType.Rotation);
        if (rot == null) {
            return;
        }

        float[] eulerAngles = rot.toAngles(null);
        eulerAngles[0] = FastMath.clamp(eulerAngles[0] + value, -0.9f * FastMath.HALF_PI, 0.9f * FastMath.HALF_PI);

        rot.fromAngles(eulerAngles);

        ClientGlobals.userCommand.look(rot);
    }

    protected void lookLeft(float value) {
        Rotation rot = (Rotation) World.INSTANCE.getComponent(ClientGlobals.playerEntity, CompType.Rotation);
        if (rot == null) {
            return;
        }

        float[] eulerAngles = rot.toAngles(null);
        eulerAngles[1] = (eulerAngles[1] + value) % FastMath.TWO_PI;

        rot.fromAngles(eulerAngles);

        ClientGlobals.userCommand.look(rot);
    }

    protected void move(float value, boolean sideways) {
        Rotation rot = (Rotation) World.INSTANCE.getComponent(ClientGlobals.playerEntity, CompType.Rotation);
        if (rot == null) {
            return;
        }

        float[] eulerAngles = rot.toAngles(null);
        eulerAngles[0] = 0f;
        if (sideways) {
            eulerAngles[1] += FastMath.HALF_PI;
        }
        eulerAngles[2] = 0f;
        Quaternion direction = new Quaternion().fromAngles(eulerAngles);

        Vector3f move = new Vector3f(0, 0, value * walkSpeed);
        direction.multLocal(move);

        ClientGlobals.userCommand.move(move.x, move.y, move.z);
    }

    private void moveShip(float x, float y, float z) {
        ClientGlobals.userCommand.shipMove(x, y, z);
    }

    private void pitch(float value) {
        ClientGlobals.userCommand.shipPitch(value);
    }

    private void roll(float value) {
        ClientGlobals.userCommand.shipRoll(value);
    }

    private void yaw(float value) {
        ClientGlobals.userCommand.shipYaw(value);
    }

    public void initWalkMode() {
        Log.info("");

        mode = CharacterMode.WALK;

        inputManager.addMapping("WALK_LookLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("WALK_LookRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("WALK_LookUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("WALK_LookDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        inputManager.addMapping("WALK_MoveForward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("WALK_MoveBackward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("WALK_MoveLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("WALK_MoveRight", new KeyTrigger(KeyInput.KEY_D));

        inputManager.addMapping("SHIP_MoveForward", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("SHIP_MoveBackward", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("SHIP_MoveUp", new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("SHIP_MoveDown", new KeyTrigger(KeyInput.KEY_PGDN));
        inputManager.addMapping("SHIP_MoveLeft", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("SHIP_MoveRight", new KeyTrigger(KeyInput.KEY_RIGHT));

        inputManager.addMapping("SHIP_PitchUp", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("SHIP_PitchDown", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("SHIP_RollCW", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("SHIP_RollCCW", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("SHIP_YawLeft", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("SHIP_YawRight", new KeyTrigger(KeyInput.KEY_O));

        inputManager.addMapping("ITEM_Pick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("ITEM_RightPick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("GAME_Exit", new KeyTrigger((KeyInput.KEY_ESCAPE)));
        inputManager.addMapping("GAME_TogglePointer", new KeyTrigger((KeyInput.KEY_F10)));

        inputManager.addListener(this, walkMappings);

        ClientGlobals.crosshair.setCullHint(Spatial.CullHint.Inherit);
    }

    private void exitWalkMode() {
        Log.info("");

        mode = CharacterMode.NONE;
        inputManager.clearMappings();

        ClientGlobals.crosshair.setCullHint(Spatial.CullHint.Always);
    }

    public void initDCPUMode() {
        Log.info("");

        mode = CharacterMode.DCPU;

        inputManager.addMapping("WALK_LookLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("WALK_LookRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("WALK_LookUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("WALK_LookDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        inputManager.addMapping("DCPU_Exit", new MouseButtonTrigger(MouseInput.BUTTON_LEFT), new KeyTrigger(KeyInput.KEY_ESCAPE));

        inputManager.addRawInputListener(dcpuListener);
        inputManager.addListener(this, dcpuMappings);
    }

    private void exitDCPUMode() {
        Log.info("");

        mode = CharacterMode.NONE;

        inputManager.clearMappings();
        inputManager.removeRawInputListener(dcpuListener);
        //inputManager.clearRawInputListeners();
    }

    public void initMenuMode() {
        Log.info("");
        
        mode = CharacterMode.MENU;
        inputManager.addMapping("MENU_Exit", new KeyTrigger((KeyInput.KEY_ESCAPE)));
        inputManager.addListener(this, menuMappings);
        
        ClientGlobals.setAppStates(DCPUMenuState.class, ECSState.class);
    }

    private void exitMenuMode() {
        Log.info("");

        mode = CharacterMode.NONE;
        inputManager.clearMappings();
        ClientGlobals.setAppStates(ECSState.class);
    }
    
    private static String[] dcpuMappings = new String[]{
        "WALK_LookLeft",
        "WALK_LookRight",
        "WALK_LookUp",
        "WALK_LookDown",
        "DCPU_Exit",
    };

    private static String[] menuMappings = new String[]{
        "MENU_Exit",
    };

    private static String[] walkMappings = new String[]{
        "WALK_LookLeft",
        "WALK_LookRight",
        "WALK_LookUp",
        "WALK_LookDown",
        "WALK_MoveForward",
        "WALK_MoveBackward",
        "WALK_MoveLeft",
        "WALK_MoveRight",
        "SHIP_MoveForward",
        "SHIP_MoveBackward",
        "SHIP_MoveUp",
        "SHIP_MoveDown",
        "SHIP_MoveLeft",
        "SHIP_MoveRight",
        "SHIP_PitchUp",
        "SHIP_PitchDown",
        "SHIP_RollCW",
        "SHIP_RollCCW",
        "SHIP_YawLeft",
        "SHIP_YawRight",
        "WALK_InvertY",
        "ITEM_Pick",
        "ITEM_RightPick",
        "GAME_Exit",
        "GAME_TogglePointer",
    };

    private void pickItem(boolean right) {
        CollisionResults results = getNodesInFrontOfCamera(); 
 
        for(int i=0; i < results.size(); i++) {
            CollisionResult closest = results.getCollision(i);
            Log.info("%d: %s", i, closest.getGeometry().getName());

            if(isForceShield(closest)) {
                // pick goes through force shield
                continue;
            }
            
            EntityNode target = getEntityNodeAncestor(closest.getGeometry());
            
            if(target == null) {
                Log.info("Action: None");
                return;
            }
            Log.info(target.getName());
            
            if(target.eid == ClientGlobals.playerEntity) {
                continue;
            }
            
            if(!target.isUsable()) {
                return;
            }

            if(target.isShip()) {
                if(target.isPlayerBase()) {
                    Vector3Int loc = getCurrentPointedBlockLocation(target.offset, closest, !right);
                    Log.info("target: " + loc.toString());
                    if(loc != null) {
                        if(right) {
                            Log.info("Action: Remove Block");
                            ClientGlobals.userCommand.unbuild(loc);
                            return;
                        } else {
                            Log.info("Action: Insert Block");
                            ClientGlobals.userCommand.build(loc);
                            return;
                        }
                    }
                } else {
                    Log.info("Action: Teleport");
                    ClientGlobals.userCommand.teleport(target.eid);
                    return;
                }
            } else {
                Log.info("Action: Pick Item");
                ClientGlobals.userCommand.pickItem(target.eid);
                return;
            }
        }
    }
    
    private void unpickItem() {
        ClientGlobals.userCommand.unpickItem();
    }

    private CollisionResults getNodesInFrontOfCamera() {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(ClientGlobals.cam.getLocation(), ClientGlobals.cam.getDirection());
        ClientGlobals.rootNode.collideWith(ray, results);
        return results;
    }

    private boolean isForceShield(CollisionResult closest) {
        return closest.getGeometry().getName().equals("forceshield");
    }

    private EntityNode getEntityNodeAncestor(Spatial spatial) {
        while(true) {
            spatial = spatial.getParent();

            if(spatial == ClientGlobals.rootNode) {
                return null;
            }

            if(spatial instanceof EntityNode) {
                EntityNode target = (EntityNode) spatial;
                
                return target;
            }
        }
    }

    private Vector3Int getCurrentPointedBlockLocation(Node node, CollisionResult closest, boolean b) {
        Vector3f collisionContactPoint = closest.getContactPoint();
        collisionContactPoint.subtractLocal(node.getLocalTranslation());
        BlockTerrainControl ctrl = node.getControl(BlockTerrainControl.class);
        return BlockNavigator.getPointedBlockLocation(ctrl, collisionContactPoint, b);
    }
}
