/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.esotericsoftware.minlog.Log;
import com.jme3.audio.AudioNode;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.Random;
import org.megastage.client.SoundManager;
import org.megastage.util.Time;

public class RandomSpinnerControl extends AbstractControl {
    public RandomSpinnerControl() {
    }

    Random rnd = new Random();
    Quaternion q = Quaternion.IDENTITY;
    long next = 0;

    private float frnd(float min, float max) {
        return (max-min) * rnd.nextFloat() + min;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(Time.value > next) {
            int mode = rnd.nextInt(20);
            if(mode == 0) {
                //AudioNode an = SoundManager.get(SoundManager.RETRO_COMPUTER);
                //an.setVolume(0.1f);
                //an.playInstance();
                next = Time.value + 5500;
            } else {
                next = Time.value + rnd.nextInt(5000)+1000;
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
