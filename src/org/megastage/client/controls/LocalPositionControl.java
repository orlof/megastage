package org.megastage.client.controls;

import org.megastage.util.Log;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class LocalPositionControl extends AbstractControl {
    private final int eid;

    public LocalPositionControl(int eid) {
        this.eid = eid; 
    }

    @Override
    protected void controlUpdate(float tpf) {
        Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
        if(pos == null) {
            Log.warn("no position component for " + eid);
            return;
        }

        spatial.setLocalTranslation(pos.get());
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
