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
    private double stime, etime;
    private Vector3f spos, epos;

    public LocalPositionControl(int eid) {
        this.eid = eid; 
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(eid == 0) {
            Log.warn("", new Exception());
        }
        
        Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
        if(pos == null) {
            Log.warn("no position component for " + eid);
            return;
        }

        if(stime < ClientGlobals.syncTime) {
            stime = ClientGlobals.syncTime;
            etime = stime + 60;
            spos = spatial.getLocalTranslation().clone();
            epos = pos.getCopy();
        }

        // interpolate
        Vector3f cur = spatial.getLocalTranslation();
        float amount = (float) ((World.INSTANCE.time - stime) / (etime - stime));
        if(amount > 1.0f) amount = 1.0f; 
        cur.interpolate(spos, epos, amount);
        spatial.setLocalTranslation(cur);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
