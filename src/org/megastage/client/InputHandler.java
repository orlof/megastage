package org.megastage.client;

import com.cubes.BlockNavigator;
import com.cubes.BlockTerrainControl;
import com.cubes.Vector3Int;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
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
import org.megastage.protocol.CharacterMode;
import org.megastage.util.Log;

public class InputHandler implements AnalogListener, ActionListener {
    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (ClientGlobals.playerEntity == 0) {
            return;
        }

        switch (name) {
            case "LOOK_Left":
                lookLeft(value);
                break;
            case "LOOK_Right":
                lookLeft(-value);
                break;
            case "LOOK_Up":
                lookUp(value * (invertY ? -1 : 1));
                break;
            case "LOOK_Down":
                lookUp(-value * (invertY ? -1 : 1));
                break;
            case "WalkForward":
                move(value, false);
                break;
            case "WALK_Backward":
                move(-value, false);
                break;
            case "WALK_Left":
                move(value, true);
                break;
            case "WALK_Right":
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
                case "ITEM_PickLeft":
                    pickItem(false);
                    break;
                case "ITEM_PickRight":
                    pickItem(true);
                    break;
                case "TEXT_Enter":
                    textEnter();
                    break;
                case "TEXT_Commit":
                    textExit();
                    break;
                case "TEXT_Cancel":
                    textExit();
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

    public void textEnter() {
        Log.mark();
        CharacterMode.change(CharacterMode.TEXT);
    }
    
    public void textExit() {
        Log.mark();
        CharacterMode.change(CharacterMode.WALK);
        // TODO action
    }
    
    public void textCancel() {
        Log.mark();
        CharacterMode.change(CharacterMode.WALK);
    }
    
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
    
    private void pickItem(boolean right) {
        CollisionResults results = getNodesInFrontOfCamera(); 
 
        for(int i=0; i < results.size(); i++) {
            CollisionResult closest = results.getCollision(i);
            Log.debug("%d: %s", i, closest.getGeometry().getName());

            if(isForceShield(closest)) {
                // pick goes through force shield
                continue;
            }
            
            EntityNode target = getEntityNodeAncestor(closest.getGeometry());
            
            if(target == null) {
                Log.info("Action: None");
                return;
            }
            Log.debug(target.getName());
            
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
