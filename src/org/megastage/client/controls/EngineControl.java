/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.EngineData;
import org.megastage.components.Position;
import org.megastage.util.ClientGlobals;

/**
 *
 * @author Orlof
 */
public class EngineControl extends AbstractControl {
    private final Entity entity;

    public EngineControl(Entity entity) {
        this.entity = entity;
        //setEnabled(true);
    }

    @Override
    protected void controlUpdate(float tpf) {
        EngineData data = entity.getComponent(EngineData.class);
        if(data != null) {
            if(data.power == 0) {
                ((ParticleEmitter) spatial).setEnabled(false);
            } else {
                ((ParticleEmitter) spatial).setEnabled(true);
                float high = (float) (0.1 + 0.9 * data.power / Character.MAX_VALUE);
                float low = (float) (0.05 + 0.095 * data.power / Character.MAX_VALUE);
                ((ParticleEmitter) spatial).setHighLife(high);
                ((ParticleEmitter) spatial).setLowLife(low);
                ((ParticleEmitter) spatial).setStartSize(low);
                ((ParticleEmitter) spatial).setEndSize(high);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
