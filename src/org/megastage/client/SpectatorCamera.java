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
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.util.Globals;
import org.megastage.util.Quaternion;
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
public class SpectatorCamera implements AnalogListener, ActionListener {

    private static String[] mappings = new String[]{
            "SPECCAM_Left",
            "SPECCAM_Right",
            "SPECCAM_Up",
            "SPECCAM_Down",

            "SPECCAM_RollLeft",
            "SPECCAM_RollRight",
            "SPECCAM_Forward",
            "SPECCAM_Backward",

            "SPECCAM_InvertY"
        };

    protected Camera cam;
    protected float rotationSpeed = 1f;
    protected float moveSpeed = 3f;
    protected boolean enabled = true;
    protected boolean invertY = true;
    protected InputManager inputManager;
    private final ArtemisState artemis;
    
    /**
     * Creates a new FlyByCamera to control the given Camera object.
     * @param cam
     */
    public SpectatorCamera(Camera cam, ArtemisState artemis){
        this.cam = cam;
        this.artemis = artemis;
    }

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
        inputManager.addMapping("SPECCAM_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("SPECCAM_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("SPECCAM_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("SPECCAM_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        // keyboard only WASD for movement and WZ for rise/lower height
        inputManager.addMapping("SPECCAM_RollLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("SPECCAM_RollRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("SPECCAM_Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("SPECCAM_Backward", new KeyTrigger(KeyInput.KEY_S));

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

    protected void rotateCamera(float value, Vector axis) {
        Log.info("rotate " + value + " " + axis.toString());
        
        Rotation r = Globals.fixedEntity.getComponent(Rotation.class);
        Quaternion q = new Quaternion(r.w, r.x, r.y, r.z);

        // rotate axis to global coordinate system
        Vector globalAxis = axis.multiply(q);

        // rotation increment in global coordinate system
        Quaternion globalRotation = new Quaternion(globalAxis, value);

        // quaternion for the new coordinate system
        Quaternion res = globalRotation.multiply(q);
        r.x = res.x;
        r.y = res.y;
        r.z = res.z;
        r.w = res.w;
    }
    
//    protected void rotateCamera(float value, Vector3f axis) {
//        Matrix3f mat = new Matrix3f();
//        mat.fromAngleNormalAxis(rotationSpeed * value, axis);
//
//        Vector3f up = cam.getUp();
//        Vector3f left = cam.getLeft();
//        Vector3f dir = cam.getDirection();
//
//        mat.mult(up, up);
//        mat.mult(left, left);
//        mat.mult(dir, dir);
//
//        Quaternion q = new Quaternion();
//        q.fromAxes(left, up, dir);
//        q.normalizeLocal();
//
//        cam.setAxes(q);
//    }

    protected void moveCamera(float value, boolean sideways) {
        Rotation r = Globals.fixedEntity.getComponent(Rotation.class);
        Quaternion q = new Quaternion(r.w, r.x, r.y, r.z);

        Vector fwd = new Vector(0.0, 0.0, -1.0);
        Vector myFwd = fwd.multiply(q);
        
        Vector vel = myFwd.multiply(value * moveSpeed);
        
        Position pos = Globals.fixedEntity.getComponent(Position.class);
        pos.x += 1000 * vel.x;
        pos.y += 1000 * vel.y;
        pos.z += 1000 * vel.z;
    }
    
//    protected void moveCamera(float value, boolean sideways) {
//        Vector3f vel = new Vector3f();
//        //Vector3f pos = cam.getLocation().clone();
//
//        if (sideways){
//            cam.getLeft(vel);
//        }else{
//            cam.getDirection(vel);
//        }
//        vel.multLocal(value * moveSpeed);
//
//        Position pos = Globals.fixedEntity.getComponent(Position.class);
//        pos.x += 1000 * vel.x;
//        pos.y += 1000 * vel.y;
//        pos.z += 1000 * vel.z;
//
//        //cam.setLocation(pos);
//    }

    private static final Vector VECTOR_UP = new Vector(0.0, 1.0, 0.0);
    private static final Vector VECTOR_LEFT = new Vector(1.0, 0.0, 0.0);
    private static final Vector VECTOR_FORWARD = new Vector(0.0, 0.0, -1.0);
    
    public void onAnalog(String name, float value, float tpf) {
        if (!enabled)
            return;

        if (name.equals("SPECCAM_Left")) {
            rotateCamera(value, VECTOR_UP);
            //rotateCamera(value, cam.getUp());
        }else if (name.equals("SPECCAM_Right")) {
            rotateCamera(-value, VECTOR_UP);
        }else if (name.equals("SPECCAM_Up")) {
            rotateCamera(-value * (invertY ? -1 : 1), VECTOR_LEFT);
        }else if (name.equals("SPECCAM_Down")){
            rotateCamera(value * (invertY ? -1 : 1), VECTOR_LEFT);
        }else if (name.equals("SPECCAM_RollLeft")) {
            rotateCamera(value, VECTOR_FORWARD);
        }else if (name.equals("SPECCAM_RollRight")){
            rotateCamera(-value, VECTOR_FORWARD);
        }else if (name.equals("SPECCAM_Forward")){
            moveCamera(value, false);
        }else if (name.equals("SPECCAM_Backward")){
            moveCamera(-value, false);
        }

//        if (name.equals("SPECCAM_Left")) {
//            rotateCamera(value, cam.getUp());
//        }else if (name.equals("SPECCAM_Right")) {
//            rotateCamera(-value, cam.getUp());
//        }else if (name.equals("SPECCAM_Up")) {
//            rotateCamera(-value * (invertY ? -1 : 1), cam.getLeft());
//        }else if (name.equals("SPECCAM_Down")){
//            rotateCamera(value * (invertY ? -1 : 1), cam.getLeft());
//        }else if (name.equals("SPECCAM_RollLeft")) {
//            rotateCamera(value, cam.getDirection());
//        }else if (name.equals("SPECCAM_RollRight")){
//            rotateCamera(-value, cam.getDirection());
//        }else if (name.equals("SPECCAM_Forward")){
//            moveCamera(value, false);
//        }else if (name.equals("SPECCAM_Backward")){
//            moveCamera(-value, false);
//        }
    }

    public void onAction(String name, boolean value, float tpf) {
        if (!enabled)
            return;

        if (name.equals("SPECCAM_InvertY")) {
            // Toggle on the up.
            if( !value ) {  
                invertY = !invertY;
            }
        }        
    }

}
