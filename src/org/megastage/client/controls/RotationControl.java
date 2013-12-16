/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Rotation;
import org.megastage.util.ClientGlobals;

public class RotationControl extends AbstractControl {
    private final Entity entity;
    
    public RotationControl(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(Log.TRACE) Log.trace("============== ROTATION " + entity.toString() + "==============");
        if(Log.TRACE) Log.trace("Parent: " + spatial.getParent().getName());
        Rotation rotation = entity.getComponent(Rotation.class);
        if(rotation == null || ClientGlobals.shipEntity == entity) {
            spatial.setLocalRotation(Quaternion.IDENTITY);            
        } else {
            Quaternion q = new Quaternion((float) rotation.x, (float) rotation.y, (float) rotation.z, (float) rotation.w);
            spatial.setLocalRotation(q);
        }
        if(Log.TRACE) {
            float[] eulerAngles = spatial.getLocalRotation().toAngles(null);
            Log.trace("Local(yaw="+(FastMath.RAD_TO_DEG * eulerAngles[0])+", roll="+(FastMath.RAD_TO_DEG * eulerAngles[1])+", pitch="+(FastMath.RAD_TO_DEG * eulerAngles[2])+")");
            spatial.getWorldRotation().toAngles(eulerAngles);
            Log.trace("World(yaw="+(FastMath.RAD_TO_DEG * eulerAngles[0])+", roll="+(FastMath.RAD_TO_DEG * eulerAngles[1])+", pitch="+(FastMath.RAD_TO_DEG * eulerAngles[2])+")");
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
