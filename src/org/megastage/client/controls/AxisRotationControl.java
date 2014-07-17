package org.megastage.client.controls;

import org.megastage.util.Log;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Rotation;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.ID;

public class AxisRotationControl extends AbstractControl {
    private final int eid;
    
    private final float[] angles = new float[3];
    private final boolean pitch;
    private final boolean yaw;
    private final boolean roll;
    
    private Quaternion curr = Quaternion.IDENTITY;
    
    public AxisRotationControl(int eid, boolean pitch, boolean yaw, boolean roll) {
        this.eid = eid;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    @Override
    protected void controlUpdate(float tpf) {
        Rotation rot  = (Rotation) World.INSTANCE.getComponent(eid, CompType.Rotation);
        if(rot == null) {
            Log.warn("no rotation component for " + eid);
            return;
        }

        if(!curr.equals(rot.value)) {
            // rotation changed
            curr = new Quaternion(rot.value);

            rot.value.toAngles(angles);
            
            if(!pitch) angles[0] = 0;
            if(!yaw) angles[1] = 0;
            if(!roll) angles[2] = 0;
            
            spatial.setLocalRotation(new Quaternion().fromAngles(angles));

            if(Log.TRACE) {
                float[] eulerAngles = spatial.getLocalRotation().toAngles(null);
                Log.info(ID.get(eid) + "Local(yaw="+(FastMath.RAD_TO_DEG * eulerAngles[0])+", roll="+(FastMath.RAD_TO_DEG * eulerAngles[1])+", pitch="+(FastMath.RAD_TO_DEG * eulerAngles[2])+")");
                spatial.getWorldRotation().toAngles(eulerAngles);
                Log.info(ID.get(eid) + "World(yaw="+(FastMath.RAD_TO_DEG * eulerAngles[0])+", roll="+(FastMath.RAD_TO_DEG * eulerAngles[1])+", pitch="+(FastMath.RAD_TO_DEG * eulerAngles[2])+")");
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
