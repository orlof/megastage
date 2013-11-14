/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.artemis.Entity;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;

/**
 *
 * @author Teppo
 */
public class PositionControl extends AbstractControl {
    private final Entity entity;

    public PositionControl(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void controlUpdate(float tpf) {
        Position position = entity.getComponent(Position.class);
        if(position != null) {
            spatial.setLocalTranslation(position.getAsVector());
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
