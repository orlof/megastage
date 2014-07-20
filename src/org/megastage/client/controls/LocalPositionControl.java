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
    private double interpolationTime, synchTime, nextSyncTime, interpolationStartTime;
    private Vector3f spos = Vector3f.ZERO, epos = Vector3f.ZERO;

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

        if(synchTime < ClientGlobals.syncTime) {
            synchTime = ClientGlobals.syncTime;
            nextSyncTime = synchTime + 60;
            spos = spatial.getLocalTranslation().clone();
            epos = pos.getCopy();
            
            if(interpolationTime == 0) {
                interpolationStartTime = ClientGlobals.syncTime;
            } else {
                interpolationStartTime = interpolationTime;
            }
        }

        interpolationTime = World.INSTANCE.time;
        
        if(World.INSTANCE.time > nextSyncTime) {
            spatial.setLocalTranslation(epos);
            return;
        }

        // interpolate
        Vector3f cur = spatial.getLocalTranslation();
        float amount = (float) ((interpolationTime - interpolationStartTime) / (nextSyncTime - interpolationStartTime));
        cur.interpolate(spos, epos, amount);

        spatial.setLocalTranslation(cur);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
