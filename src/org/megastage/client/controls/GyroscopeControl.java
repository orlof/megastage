package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.LightNode;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.transfer.GyroscopeData;

/**
 *
 * @author Orlof
 */
public class GyroscopeControl extends AbstractControl {
    private final Entity entity;
    private GyroscopeData data;
    
    private char power = 0;
    private float angularSpeed = 0;

    public GyroscopeControl(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(data == null) {
            data = entity.getComponent(GyroscopeData.class);
            if(data == null) {
                return;
            }
        }

        if(power != data.power) {
            power = data.power;
            angularSpeed = 5.0f * data.getAngularSpeed();
            Log.info("angular speed: " + Math.toDegrees(angularSpeed));
        }

        float angle = angularSpeed * tpf;
        spatial.rotate(0, 0, angle);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
