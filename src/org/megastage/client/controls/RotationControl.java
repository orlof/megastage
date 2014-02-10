package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Rotation;
import org.megastage.client.ClientGlobals;

public class RotationControl extends AbstractControl {
    private final Entity entity;
    private Rotation rot;
    
    public RotationControl(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(rot == null) {
            rot = entity.getComponent(Rotation.class);
            if(rot == null) {
                return;
            }
        }

        if(ClientGlobals.shipEntity == entity) {
            spatial.setLocalRotation(Quaternion.IDENTITY);            
        } else if (rot.dirty) {
            Quaternion q = new Quaternion((float) rot.x, (float) rot.y, (float) rot.z, (float) rot.w);
            spatial.setLocalRotation(q);
            rot.dirty = false;

            if(Log.TRACE) {
                float[] eulerAngles = spatial.getLocalRotation().toAngles(null);
                Log.info("Local(yaw="+(FastMath.RAD_TO_DEG * eulerAngles[0])+", roll="+(FastMath.RAD_TO_DEG * eulerAngles[1])+", pitch="+(FastMath.RAD_TO_DEG * eulerAngles[2])+")");
                spatial.getWorldRotation().toAngles(eulerAngles);
                Log.info("World(yaw="+(FastMath.RAD_TO_DEG * eulerAngles[0])+", roll="+(FastMath.RAD_TO_DEG * eulerAngles[1])+", pitch="+(FastMath.RAD_TO_DEG * eulerAngles[2])+")");
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
