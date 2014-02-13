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
import org.megastage.util.Mapper;

public class RotationControl extends AbstractControl {
    private final Entity entity;
    private Rotation rot;
    
    public RotationControl(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(rot == null) {
            rot = Mapper.ROTATION.get(entity);
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
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
