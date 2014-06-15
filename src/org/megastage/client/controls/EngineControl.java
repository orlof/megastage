package org.megastage.client.controls;

import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.client.SoundManager;
import org.megastage.components.transfer.EngineData;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class EngineControl extends AbstractControl {
    private final int eid;
    
    private int power = -1;
    private final AudioNode an;
    
    public EngineControl(int eid) {
        this.eid = eid;
        this.an = SoundManager.get(SoundManager.SPACE_ENGINE).clone();
        an.setLooping(true);
     }

    @Override
    protected void controlUpdate(float tpf) {
        EngineData data = (EngineData) World.INSTANCE.getComponent(eid, CompType.EngineData);
        assert data != null;
        
        if(power != data.power) {
            ParticleEmitter emitter = (ParticleEmitter) spatial;

            power = data.power;

            an.setVolume(((float) power) / 6553.6f);
            
            if(power == 0) {
                an.pause();
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
