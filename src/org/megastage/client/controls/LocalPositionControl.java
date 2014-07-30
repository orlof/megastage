package org.megastage.client.controls;

import com.jme3.math.Vector3f;
import org.megastage.util.Log;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.client.ClientGlobals;
import org.megastage.components.Position;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class LocalPositionControl extends AbstractControl {
    private final int eid;

    private long etime = 0;

    public LocalPositionControl(int eid) {
        this.eid = eid; 
    }

    @Override
    protected void controlUpdate(float tpf) {
        Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
        if(pos == null) {
            Log.warn("Position for [%d] is missing", eid);
            return;
        }

        long ctime = World.INSTANCE.time;

        if(pos.isDirty()) {
            pos.setDirty(false);
            etime = ctime + 50;
        }

        if(ctime > etime) {
            spatial.setLocalTranslation(pos.get());
            return;
        }
        
        // interpolate
        float timeLeft = ((float) etime - ctime) / 1000.0f;
        float amount = tpf / (timeLeft + tpf);
        Vector3f current = spatial.getLocalTranslation().clone();
        spatial.setLocalTranslation(current.interpolate(pos.get(), amount));
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
