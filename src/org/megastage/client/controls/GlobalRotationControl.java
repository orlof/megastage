package org.megastage.client.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Rotation;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class GlobalRotationControl extends AbstractControl {
    
    public GlobalRotationControl() {
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(ClientGlobals.playerParentEntity == 0) {
            return;
        }

        Rotation rot = (Rotation) World.INSTANCE.getComponent(ClientGlobals.playerParentEntity, CompType.Rotation);
        assert rot != null;

        spatial.setLocalRotation(rot.value.inverse());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}

