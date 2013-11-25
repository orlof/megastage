/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.esotericsoftware.minlog.Log;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Rotation;
import org.megastage.util.Globals;

public class SystemRotationControl extends AbstractControl {
    
    public SystemRotationControl() {
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(Globals.fixedEntity == null) {
            Log.warn("No fixed entity");
            spatial.setLocalRotation(Quaternion.IDENTITY);            
            return;
        }

        Rotation rotation = Globals.fixedEntity.getComponent(Rotation.class);
        if(rotation == null) {
            spatial.setLocalRotation(Quaternion.IDENTITY);            
        } else {
            Quaternion q = new Quaternion((float) rotation.x, (float) rotation.y, (float) rotation.z, (float) rotation.w).inverse();
            if(q != null) {
                spatial.setLocalRotation(q);
            } else {
                Log.info("Warning: setting non invertable rotation");
            }
        }
        Log.debug(Globals.fixedEntity.getId() + " <- " + spatial.getLocalRotation().toString());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}

