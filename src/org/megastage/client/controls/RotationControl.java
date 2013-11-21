/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.artemis.Entity;
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
        Rotation rotation = entity.getComponent(Rotation.class);
        if(rotation != null) {
            if(Globals.fixedEntity == entity) {
                spatial.setLocalRotation(Quaternion.ZERO);
            } else {
                Quaternion q = new Quaternion(rotation.x, rotation.y, rotation.z, rotation.w);
                spatial.setLocalRotation(q);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
