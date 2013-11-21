/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;
import org.megastage.util.Globals;

/**
 *
 * @author Teppo
 */
public class PositionControl extends AbstractControl {
    private final Entity entity;

    public PositionControl(Entity entity) {
        this.entity = entity;
        setEnabled(true);
    }

    @Override
    protected void controlUpdate(float tpf) {
        Log.info("==============" + entity.toString() + "==============");
        Log.info("Spatial is " + spatial.getName());
        Log.info("Spatial is child of " + spatial.getParent().getName());
        Log.info("local" + spatial.getLocalTranslation().toString());
        Log.info("world" + spatial.getWorldTranslation().toString());
        Position position = entity.getComponent(Position.class);
        if(position != null) {
            if(Globals.fixedEntity == entity) {
                spatial.setLocalTranslation(Vector3f.ZERO);
            } else {
                Log.info("SET");
                Vector3f vpos = position.getAsVector();
                spatial.setLocalTranslation(vpos);
            }
            
            Log.info("local" + spatial.getLocalTranslation().toString());
            Log.info("world" + spatial.getWorldTranslation().toString());
            
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
