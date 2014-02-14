package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.audio.AudioNode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.client.SoundManager;
import org.megastage.components.transfer.GyroscopeData;
import org.megastage.util.Mapper;

/**
 *
 * @author Orlof
 */
public class GyroscopeControl extends AbstractControl {
    private final Entity entity;
    private GyroscopeData data;
    
    private char power = 0;
    private float angularSpeed = 0;

    private AudioNode an;
    
    public GyroscopeControl(Entity entity) {
        this.entity = entity;
        this.an = SoundManager.get(SoundManager.GYROSCOPE).clone();
        an.setLooping(true);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(data == null) {
            data = Mapper.GYROSCOPE_DATA.get(entity);
            if(data == null) {
                return;
            }
        }

        if(power != data.power) {
            if(power == 0) {
                an.play();
            }

            power = data.power;
            angularSpeed = 5.0f * data.getAngularSpeed();

            an.setVolume(Math.abs(angularSpeed) * 2);
            
            if(power == 0) {
                an.pause();
            }
            
            Log.info("angular speed: " + Math.toDegrees(angularSpeed));
        }

        float angle = angularSpeed * tpf;
        spatial.rotate(0, 0, angle);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
