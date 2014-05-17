package org.megastage.client.controls;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class SystemPositionControl extends AbstractControl {

    public SystemPositionControl() {
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(ClientGlobals.shipEntity == 0) {
            spatial.setLocalTranslation(Vector3f.ZERO);
            return;
        }

        Position position = (Position) World.INSTANCE.getComponent(ClientGlobals.shipEntity, CompType.Position);
        if(position == null) {
            spatial.setLocalTranslation(Vector3f.ZERO);
        } else {
            spatial.setLocalTranslation(position.getVector3f().negate());
        }
        //Log.trace("System Local " + spatial.toString() + " " + spatial.getLocalTranslation().toString());
        //Log.trace("System World " + spatial.toString() + " " + spatial.getWorldTranslation().toString());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}

