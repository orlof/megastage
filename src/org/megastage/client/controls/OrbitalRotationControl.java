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
import org.megastage.components.OrbitalRotation;

public class OrbitalRotationControl extends AbstractControl {
    private final Entity entity;
    
    public OrbitalRotationControl(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void controlUpdate(float tpf) {
        OrbitalRotation orbitalRotation = entity.getComponent(OrbitalRotation.class);
        spatial.setLocalRotation(new Quaternion().fromAngles(0f, (float) orbitalRotation.getAngle(), 0f));
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}

