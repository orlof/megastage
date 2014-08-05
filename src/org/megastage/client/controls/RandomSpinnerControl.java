package org.megastage.client.controls;

import com.jme3.audio.AudioNode;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.Random;
import org.megastage.client.SoundManager;
import org.megastage.ecs.World;

public class RandomSpinnerControl extends AbstractControl {
    public RandomSpinnerControl() {
    }

    private static final Random rnd = new Random();
    private Quaternion q = Quaternion.IDENTITY;
    private long next = 0;

    private float frnd(float min, float max) {
        return (max-min) * rnd.nextFloat() + min;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(World.INSTANCE.time > next) {
            int mode = rnd.nextInt(20);
            if(mode == 0) {
                AudioNode an = SoundManager.get(SoundManager.RETRO_COMPUTER);
                an.setVolume(0.1f);
                an.playInstance();
                next = World.INSTANCE.time + 5500;
            } else {
                next = World.INSTANCE.time + rnd.nextInt(5000)+1000;
            }
            
            if(mode < 8) {
                q = Quaternion.IDENTITY;
            } else {
                q = new Quaternion().fromAngles(frnd(0, 5), frnd(0, 5), frnd(0, 5));
            }
        }

        Quaternion tmp = Quaternion.IDENTITY.clone();
        tmp.slerp(q, tpf);
        spatial.rotate(tmp);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
