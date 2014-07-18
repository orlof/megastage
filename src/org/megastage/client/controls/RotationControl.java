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
    
    public RotationControl(int eid) {
         this.eid = eid; 
    }

    @Override
    protected void controlUpdate(float tpf) {
        Rotation rot = (Rotation) World.INSTANCE.getComponent(eid, CompType.Rotation);
        if(rot==null) {
            return;
        }

        if(ClientGlobals.baseEntity == eid) {
            spatial.setLocalRotation(Quaternion.IDENTITY);            
        } else if (rot.isDirty()) {
            spatial.setLocalRotation(rot.value);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
