/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.esotericsoftware.minlog.Log;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;
import org.megastage.util.Globals;

public class SystemPositionControl extends AbstractControl {
    
    public SystemPositionControl() {
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(Globals.fixedEntity == null) {
            Log.warn("No fixed entity");
            spatial.setLocalTranslation(Vector3f.ZERO);
            return;
        }

        Position position = Globals.fixedEntity.getComponent(Position.class);
        if(position == null) {
            spatial.setLocalTranslation(Vector3f.ZERO);
        } else {
            spatial.setLocalTranslation(position.getAsVector().negate());
        }
        Log.debug(Globals.fixedEntity.getId() + " <- " + spatial.getLocalTranslation().toString());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}

