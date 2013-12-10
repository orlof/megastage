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
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.util.ClientGlobals;
import org.megastage.util.Vector;


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

    private static String[] mappings = new String[]{
            "WALK_LookLeft",
            "WALK_LookRight",
            "WALK_LookUp",
            "WALK_LookDown",

            "WALK_MoveForward",
            "WALK_MoveBackward",

            "WALK_InvertY"
        };

    protected float rotationSpeed = 1f;
    protected float moveSpeed = 3f;
    protected boolean enabled = true;
    protected boolean invertY = false;
    protected InputManager inputManager;
    
    /**
     * Sets the move speed. The speed is given in world units per second.
     * @param moveSpeed
     */
    public void setMoveSpeed(float moveSpeed){
        this.moveSpeed = moveSpeed;
    }
    
    /**
     * Gets the move speed. The speed is given in world units per second.
     * @return moveSpeed
     */
    public float getMoveSpeed(){
        return moveSpeed;
    }

    /**
     * Sets the rotation speed.
     * @param rotationSpeed
     */
    public void setRotationSpeed(float rotationSpeed){
        this.rotationSpeed = rotationSpeed;
    }
    
    /**
     * Gets the move speed. The speed is given in world units per second.
     * @return rotationSpeed
     */
    public float getRotationSpeed(){
        return rotationSpeed;
    }
    
    /**
     * @param enable If false, the camera will ignore input.
     */
    public void setEnabled(boolean enable){
        if (enabled && !enable){
            if (inputManager!= null){
                inputManager.setCursorVisible(true);
            }
        }
        enabled = enable;
    }

    /**
     * @return If enabled
     * @see FlyByCamera#setEnabled(boolean)
     */
    public boolean isEnabled(){
        return enabled;
    }

    /**
     * Registers the FlyByCamera to receive input events from the provided
     * Dispatcher.
     * @param inputManager
     */
    public void registerWithInput(InputManager inputManager){
        this.inputManager = inputManager;
        
        // both mouse and button - rotation of cam
        inputManager.addMapping("WALK_LookLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("WALK_LookRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("WALK_LookUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("WALK_LookDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        // keyboard only WASD for movement and WZ for rise/lower height
        inputManager.addMapping("WALK_MoveForward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("WALK_MoveBackward", new KeyTrigger(KeyInput.KEY_S));

        inputManager.addListener(this, mappings);
        inputManager.setCursorVisible(false);
    }

    /**
     * Registers the FlyByCamera to receive input events from the provided
     * Dispatcher.
     * @param inputManager
     */
    public void unregisterInput(){
    
        if (inputManager == null) {
            return;
        }
    
        for (String s : mappings) {
            if (inputManager.hasMapping(s)) {
                inputManager.deleteMapping( s );
            }
        }

        inputManager.removeListener(this);
        inputManager.setCursorVisible(false);
    }

    protected void lookUp(float value) {
        Rotation playerRotation = ClientGlobals.playerEntity.getComponent(Rotation.class);
        if(playerRotation == null) return;

        Quaternion playerQuaternion = new Quaternion(
                (float) playerRotation.x, (float) playerRotation.y, 
                (float) playerRotation.z, (float) playerRotation.w);

        float[] eulerAngles = playerQuaternion.toAngles(null);
        eulerAngles[2] = FastMath.clamp(eulerAngles[2] + value, -FastMath.HALF_PI, FastMath.HALF_PI);
        
        playerQuaternion.fromAngles(eulerAngles);

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
        eulerAngles[0] = (eulerAngles[0] + value) % FastMath.TWO_PI;
        
        playerQuaternion.fromAngles(eulerAngles);

        playerRotation.x = playerQuaternion.getX();
        playerRotation.y = playerQuaternion.getY();
        playerRotation.z = playerQuaternion.getZ();
        playerRotation.w = playerQuaternion.getW();
    }
    
    protected void move(float value) {
        Rotation playerRotation = ClientGlobals.playerEntity.getComponent(Rotation.class);
        if(playerRotation == null) return;

        Quaternion playerQuaternion = new Quaternion(
                (float) playerRotation.x, (float) playerRotation.y, 
                (float) playerRotation.z, (float) playerRotation.w);

        float[] eulerAngles = playerQuaternion.toAngles(null);
        eulerAngles[1] = 0f;
        eulerAngles[2] = 0f;
        playerQuaternion.fromAngles(eulerAngles);
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
                move(value);
                break;
            case "WALK_MoveBackward":
                move(-value);
                break;
        }
    }

    @Override
    public void onAction(String name, boolean value, float tpf) {
        if (!enabled)
            return;

        if (name.equals("WALK_InvertY")) {
            // Toggle on the up.
            if( !value ) {  
                invertY = !invertY;
            }
        }        
    }

}
