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
import org.megastage.client.ClientGlobals;

public class SystemPositionControl extends AbstractControl {

    public SystemPositionControl() {
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(ClientGlobals.shipEntity == null) {
            spatial.setLocalTranslation(Vector3f.ZERO);
            return;
        }

        Position position = ClientGlobals.shipEntity.getComponent(Position.class);
        if(position == null) {
            spatial.setLocalTranslation(Vector3f.ZERO);
        } else {
            spatial.setLocalTranslation(position.getVector3f().negate());
        }
        Log.trace("System Local " + spatial.toString() + " " + spatial.getLocalTranslation().toString());
        Log.trace("System World " + spatial.toString() + " " + spatial.getWorldTranslation().toString());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}

