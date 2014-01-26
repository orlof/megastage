/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.transfer.EngineData;

/**
 *
 * @author Orlof
 */
public class EngineControl extends AbstractControl {
    private final Entity entity;
    
    private int power = -1;

    public EngineControl(Entity entity) {
        this.entity = entity;
        //setEnabled(true);
    }

    @Override
    protected void controlUpdate(float tpf) {
        EngineData data = entity.getComponent(EngineData.class);
        if(data != null && power != data.power) {
            ParticleEmitter emitter = (ParticleEmitter) spatial;

            power = data.power;

            if(power == 0) {
                emitter.emitAllParticles();
                emitter.setParticlesPerSec(0);
            } else {
                emitter.setParticlesPerSec(150);
                float high = (float) (0.1 + 0.9 * power / Character.MAX_VALUE);
                float low = (float) (0.05 + 0.095 * power / Character.MAX_VALUE);
                
                emitter.setHighLife(high);
                emitter.setLowLife(low);
                emitter.setStartSize(low);
                emitter.setEndSize(high);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
