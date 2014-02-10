/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.artemis.Entity;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.transfer.GyroscopeData;

/**
 *
 * @author Orlof
 */
public class GyroscopeControl extends AbstractControl {
    private final Entity entity;
    
    private int power = -1;

    public GyroscopeControl(Entity entity) {
        this.entity = entity;
        //setEnabled(true);
    }

    @Override
    protected void controlUpdate(float tpf) {
        GyroscopeData data = entity.getComponent(GyroscopeData.class);
        if(data != null && power != data.power) {
            // TODO
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
