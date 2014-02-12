package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Rotation;

public class CharacterRotationControl extends AbstractControl {
    private final Entity entity;
    private Rotation rot;
    
    float[] angles = new float[3];
    
    public CharacterRotationControl(Entity entity) {
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

        if (rot.dirty) {
            rot.dirty = false;

            Quaternion q = new Quaternion((float) rot.x, (float) rot.y, (float) rot.z, (float) rot.w);
            q.toAngles(angles);
            
            ((Node) spatial).getChild("head").setLocalRotation(q.clone().fromAngles(angles[0], 0, 0));
            spatial.setLocalRotation(q.fromAngles(0, angles[1], 0));
            

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
