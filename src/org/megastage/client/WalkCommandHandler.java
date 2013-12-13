/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import com.esotericsoftware.minlog.Log;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.protocol.UserCommand;
import org.megastage.util.ClientGlobals;


/**
 * A first person view camera controller.
 * After creation, you must register the camera controller with the
 * dispatcher using #registerWithDispatcher().
 *
 * Controls:
 *  - Move the mouse to rotate the camera
 *  - Mouse wheel for zooming in or out
 *  - WASD keys for moving forward/backward and strafing
 *  - QZ keys raise or lower the camera
 */
public class WalkCommandHandler implements AnalogListener, ActionListener {

    private static String[] dcpuMappings = new String[] {
        "DCPU_Exit"
    };

    private static String[] walkMappings = new String[] {
        "WALK_LookLeft",
        "WALK_LookRight",
        "WALK_LookUp",
        "WALK_LookDown",

        "WALK_MoveForward",
        "WALK_MoveBackward",
        "WALK_MoveLeft",
        "WALK_MoveRight",

        "WALK_InvertY",
        "DCPU_Enter",
        "TEST",

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
    };

    private String[] currentMappings = walkMappings;
        
    protected float rotationSpeed = 1f;
    protected float walkSpeed = 3f;
    protected float shipSpeed = 10000f;
    protected boolean enabled = true;
    protected boolean invertY = true;
    protected InputManager inputManager;
    
    public void setWalkSpeed(float walkSpeed){
        this.walkSpeed = walkSpeed;
    }
    
    public float getWalkSpeed(){
        return walkSpeed;
    }

    public void setRotationSpeed(float rotationSpeed){
        this.rotationSpeed = rotationSpeed;
    }
    
    public float getRotationSpeed(){
        return rotationSpeed;
    }
    
    public void setEnabled(boolean enable){
        if (enabled && !enable){
            if (inputManager!= null){
                inputManager.setCursorVisible(true);
            }
        }
        enabled = enable;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public void registerWithInput(InputManager inputManager){
        this.inputManager = inputManager;
 
        enableWalkMappings();
        
        inputManager.addListener(this, walkMappings);
        inputManager.setCursorVisible(false);
    }

    public void unregisterInput(){
    
        if (inputManager == null) {
            return;
        }
        
        disableWalkMappings();
        disableDCPUMappings();
    
        inputManager.removeListener(this);
        inputManager.setCursorVisible(false);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (!enabled)
            return;
        
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
                moveShip(0, 0, value);
                break;
            case "SHIP_MoveBackward":
                moveShip(0, 0, -value);
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
        if (!enabled)
            return;
        switch (name) {
            case "WALK_InvertY":
                // Toggle on the up.
                if( !value ) {  
                    invertY = !invertY;
                }
                break;
            case "DCPU_Enter":
                // Toggle on the up.
                if( !value ) {  
                    toggleDCPU();
                }        
                break;
            case "DCPU_Exit":
                // Toggle on the up.
                if( !value ) {  
                    toggleDCPU();
                }        
                break;
            case "TEST":
                // Toggle on the up.
                Log.info("SPACE");
                break;
        }
    }

    protected void lookUp(float value) {
        Rotation playerRotation = ClientGlobals.playerEntity.getComponent(Rotation.class);
        if(playerRotation == null) return;

        Quaternion playerQuaternion = new Quaternion(
                (float) playerRotation.x, (float) playerRotation.y, 
                (float) playerRotation.z, (float) playerRotation.w);

        float[] eulerAngles = playerQuaternion.toAngles(null);
        eulerAngles[0] = FastMath.clamp(eulerAngles[0] + value, -0.9f * FastMath.HALF_PI, 0.9f * FastMath.HALF_PI);
        eulerAngles[2] = 0f;
        
        playerQuaternion.fromAngles(eulerAngles).normalizeLocal();

        playerRotation.x = playerQuaternion.getX();
        playerRotation.y = playerQuaternion.getY();
        playerRotation.z = playerQuaternion.getZ();
        playerRotation.w = playerQuaternion.getW();
    }
    
    protected void lookLeft(float value) {
        Rotation playerRotation = ClientGlobals.playerEntity.getComponent(Rotation.class);
        if(playerRotation == null) return;

        Quaternion playerQuaternion = new Quaternion(
                (float) playerRotation.x, (float) playerRotation.y, 
                (float) playerRotation.z, (float) playerRotation.w);

        float[] eulerAngles = playerQuaternion.toAngles(null);
        eulerAngles[1] = (eulerAngles[1] + value) % FastMath.TWO_PI;
        eulerAngles[2] = 0f;
        
        playerQuaternion.fromAngles(eulerAngles).normalizeLocal();

        playerRotation.x = playerQuaternion.getX();
        playerRotation.y = playerQuaternion.getY();
        playerRotation.z = playerQuaternion.getZ();
        playerRotation.w = playerQuaternion.getW();
    }
    
    protected void move(float value, boolean sideways) {
        Rotation playerRotation = ClientGlobals.playerEntity.getComponent(Rotation.class);
        if(playerRotation == null) return;

        Quaternion playerQuaternion = new Quaternion(
                (float) playerRotation.x, (float) playerRotation.y, 
                (float) playerRotation.z, (float) playerRotation.w);

        float[] eulerAngles = playerQuaternion.toAngles(null);
        eulerAngles[0] = 0f;
        if(sideways) eulerAngles[1] += FastMath.HALF_PI;
        eulerAngles[2] = 0f;
        playerQuaternion.fromAngles(eulerAngles).normalizeLocal();
        
        Vector3f playerMovement = new Vector3f(0, 0, value * walkSpeed);
        playerQuaternion.multLocal(playerMovement);
        
        ClientGlobals.userCommand.move(playerMovement.x, playerMovement.z);
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

    private boolean dcpu = false;
    private DCPURawInputListener dcpuListener = new DCPURawInputListener();

    void toggleDCPU() {
        dcpu = !dcpu;
        if(dcpu) {
            disableWalkMappings();
            enableDCPUMappings();
            inputManager.removeListener(this);
            inputManager.addRawInputListener(dcpuListener);
            inputManager.addListener(this, dcpuMappings);
        } else {
            inputManager.removeRawInputListener(dcpuListener);
            inputManager.removeListener(this);
            inputManager.addListener(this, walkMappings);
            disableDCPUMappings();
            enableWalkMappings();
        }
    }

    private void enableWalkMappings() {
       // both mouse and button - rotation of cam
        inputManager.addMapping("WALK_LookLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("WALK_LookRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("WALK_LookUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("WALK_LookDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        // keyboard only WASD for movement and WZ for rise/lower height
        inputManager.addMapping("WALK_MoveForward", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("WALK_MoveBackward", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("WALK_MoveLeft", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("WALK_MoveRight", new KeyTrigger(KeyInput.KEY_RIGHT));

        inputManager.addMapping("SHIP_MoveForward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("SHIP_MoveBackward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("SHIP_MoveUp", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("SHIP_MoveDown", new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("SHIP_MoveLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("SHIP_MoveRight", new KeyTrigger(KeyInput.KEY_D));
        
        inputManager.addMapping("SHIP_PitchUp", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("SHIP_PitchDown", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("SHIP_RollCW", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("SHIP_RollCCW", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("SHIP_YawLeft", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("SHIP_YawRight", new KeyTrigger(KeyInput.KEY_O));

        inputManager.addMapping("DCPU_Enter", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("TEST", new KeyTrigger(KeyInput.KEY_SPACE));
     }

     private void enableDCPUMappings() {
         inputManager.addMapping("DCPU_Exit", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
     }
    
    public void disableWalkMappings() {
        for (String s : walkMappings) {
            if (inputManager.hasMapping(s)) {
                inputManager.deleteMapping( s );
            }
        }
    }

    public void disableDCPUMappings() {
        for (String s : dcpuMappings) {
            if (inputManager.hasMapping(s)) {
                inputManager.deleteMapping( s );
            }
        }
    }
    
}
