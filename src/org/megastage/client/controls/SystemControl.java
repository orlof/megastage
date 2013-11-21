/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.esotericsoftware.minlog.Log;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.util.Globals;

public class SystemControl extends AbstractControl {
    
    public SystemControl() {
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(Globals.fixedEntity == null) {
            Log.error("No fixed entity");
            
            spatial.setLocalTranslation(Vector3f.ZERO);
            spatial.setLocalRotation(Quaternion.ZERO);            
            return;
        }

        Position position = Globals.fixedEntity.getComponent(Position.class);
        if(position == null) {
            spatial.setLocalTranslation(Vector3f.ZERO);
        } else {
            spatial.setLocalTranslation(position.getAsVector().negate());
        }
        Log.info(Globals.fixedEntity.getId() + " <- " + spatial.getLocalTranslation().toString());

        Rotation rotation = Globals.fixedEntity.getComponent(Rotation.class);
        if(rotation == null) {
            spatial.setLocalRotation(Quaternion.ZERO);            
        } else {
            Quaternion q = new Quaternion(rotation.x, rotation.y, rotation.z, rotation.w).inverse();
            if(q != null) {
                spatial.setLocalRotation(q);
            } else {
                Log.info("Warning: setting non invertable rotation");
            }
        }
        Log.info(Globals.fixedEntity.getId() + " <- " + spatial.getLocalRotation().toString());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}

