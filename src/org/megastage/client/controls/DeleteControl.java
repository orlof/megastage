package org.megastage.client.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.util.Time;

public class DeleteControl extends AbstractControl {
    private final long expirationTime;

    public DeleteControl(long expirationDelay) {
        this.expirationTime = Time.value + expirationDelay;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(Time.value > expirationTime) {
            spatial.removeFromParent();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
