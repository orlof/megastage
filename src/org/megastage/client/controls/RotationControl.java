/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Rotation;
import org.megastage.util.Globals;

public class RotationControl extends AbstractControl {
    private final Entity entity;
    
    public RotationControl(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void controlUpdate(float tpf) {
        Log.info("============== ROTATION " + entity.toString() + "==============");
        Log.info("Spatial is " + spatial.getName());
        Log.info("Spatial is child of " + spatial.getParent().getName());
        Rotation rotation = entity.getComponent(Rotation.class);
        if(rotation != null) {
            if(Globals.fixedEntity == entity) {
                spatial.setLocalRotation(Quaternion.IDENTITY);
            } else {
                Quaternion q = new Quaternion().fromAngles(0, rotation.y, 0);
                spatial.setLocalRotation(q);
            }
            Log.info("Local" + spatial.getLocalRotation().toString());
            Log.info("World" + spatial.getWorldRotation().toString());
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
