package org.megastage.client.controls;

import org.megastage.util.Log;
import com.jme3.audio.AudioNode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.client.SoundManager;
import org.megastage.components.transfer.GyroscopeData;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class GyroscopeControl extends AbstractControl {
    private final int eid;
    
    private int signedValue = 0;
    private float angularSpeed = 0;

    private AudioNode an;
    
    public GyroscopeControl(int eid) {
        this.eid = eid;
        this.an = SoundManager.get(SoundManager.GYROSCOPE).clone();
        an.setLooping(true);
    }

    @Override
    protected void controlUpdate(float tpf) {
        GyroscopeData data = (GyroscopeData) World.INSTANCE.getComponent(eid, CompType.GyroscopeData);
        assert data != null;

        if(signedValue != data.signedValue) {
            if(signedValue == 0) {
                an.play();
            }

            signedValue = data.signedValue;
            angularSpeed = 5.0f * data.getAngularSpeed();

            an.setVolume(Math.abs(angularSpeed) * 2);
            
            if(signedValue == 0) {
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
