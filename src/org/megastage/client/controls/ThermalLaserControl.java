package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.megastage.client.SoundManager;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.components.transfer.EngineData;
import org.megastage.components.transfer.ThermalLaserData;
import org.megastage.util.Mapper;

/**
 *
 * @author Orlof
 */
public class ThermalLaserControl extends AbstractControl {
    private final Entity entity;
    private final AudioNode an;
    private char status;

    public ThermalLaserControl(Entity entity) {
        this.entity = entity;
        this.an = SoundManager.get(SoundManager.LASER_BEAM).clone();
        an.setLooping(true);
     }

    @Override
    protected void controlUpdate(float tpf) {
        ThermalLaserData data = Mapper.THERMAL_LASER_DATA.get(entity);
        if(data != null && status != data.status) {
            switch(data.status) {
                case VirtualThermalLaser.STATUS_FIRING:
                    an.play();
                    spatial.setCullHint(Spatial.CullHint.Inherit);
                    break;
                case VirtualThermalLaser.STATUS_COOLDOWN:
                    an.pause();
                    spatial.setCullHint(Spatial.CullHint.Always);
                    break;
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
