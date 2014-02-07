/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.Random;
import org.megastage.util.Time;

public class RandomSpinnerControl extends AbstractControl {
    public RandomSpinnerControl() {
    }

    Random rnd = new Random();
    Quaternion q;
    long next = 0;

    private float frnd() {
        return rnd.nextFloat()/100f;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(Time.value > next) {
            next = Time.value + rnd.nextInt(5000)+1000;
            q = new Quaternion().fromAngles(frnd(), frnd(), frnd());
        }

        spatial.rotate(q);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
