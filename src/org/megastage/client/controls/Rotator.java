/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.esotericsoftware.minlog.Log;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class Rotator extends AbstractControl {
    
    Quaternion qq;
    
    public Rotator() {
        qq = new Quaternion().fromAngleAxis(0.1f, Vector3f.UNIT_Y);
    }

    @Override
    protected void controlUpdate(float tpf) {
        tpf /= 5f;
        Log.info("ROTATOR");
        spatial.rotate(0, tpf, 0);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
