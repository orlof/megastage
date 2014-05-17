package org.megastage.client.controls;

import com.esotericsoftware.minlog.Log;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.client.ClientGlobals;
import org.megastage.components.Rotation;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.ID;

public class AxisRotationControl extends AbstractControl {
    private final int eid;
    private Rotation rot;
    
    private final float[] angles = new float[3];
    private final boolean pitch;
    private final boolean yaw;
    private final boolean roll;
    
    double x=0, y=0, z=0, w=0;
    
    public AxisRotationControl(int eid, boolean pitch, boolean yaw, boolean roll) {
        this.eid = eid;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(rot == null) {
            rot = (Rotation) World.INSTANCE.getComponent(eid, CompType.Rotation);
            if(rot == null) {
                return;
            }
        }

        if (rot.x != x || rot.y != y || rot.z != z || rot.w != w) {
            x = rot.x; y = rot.y; z = rot.z; w = rot.w;

            Quaternion q = new Quaternion((float) rot.x, (float) rot.y, (float) rot.z, (float) rot.w);
            q.toAngles(angles);
            
            if(!pitch) angles[0] = 0;
            if(!yaw) angles[1] = 0;
            if(!roll) angles[2] = 0;
            
            spatial.setLocalRotation(q.fromAngles(angles));

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
