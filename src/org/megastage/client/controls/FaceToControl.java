/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.artemis.Entity;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;

public class FaceToControl extends AbstractControl {
    private final Entity entity;
    
    public FaceToControl(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(entity == null) return;
        
        Position pos = entity.getComponent(Position.class);
        if(pos == null) return;
        
        Vector3f coord = pos.getVector3f();
        spatial.lookAt(coord, Vector3f.UNIT_Y);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
