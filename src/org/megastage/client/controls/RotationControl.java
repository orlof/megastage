package org.megastage.client.controls;

import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Rotation;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class RotationControl extends AbstractControl {
    private final int eid;
    private Rotation rot;
    
    public RotationControl(int eid) {
         this.eid = eid; 
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(rot == null) {
            rot = (Rotation) World.INSTANCE.getComponent(eid, CompType.Rotation);
            if(rot == null) {
                return;
            }
        }

        if(ClientGlobals.shipEntity == eid) {
            spatial.setLocalRotation(Quaternion.IDENTITY);            
        } else if (rot.dirty) {
            Quaternion q = rot.getQuaternion3f();
            spatial.setLocalRotation(q);
            rot.dirty = false;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
