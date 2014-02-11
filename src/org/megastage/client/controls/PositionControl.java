package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;
import org.megastage.client.ClientGlobals;
import org.megastage.util.Time;

/**
 *
 * @author Orlof
 */
public class PositionControl extends AbstractControl {
    private final Entity entity;
    private Position pos;

    private double st, sx, sy, sz;
    private double et, ex, ey, ez;

    private long x, y, z, t, t2;
    
    public PositionControl(Entity entity) {
        this.entity = entity;
        setEnabled(true);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(pos == null) {
            pos = entity.getComponent(Position.class);
            if(pos == null) {
                return;
            }
        }

        if(ClientGlobals.shipEntity == entity) {
            spatial.setLocalTranslation(0,0,0);
            return;
        }

        if(pos.x != x || pos.y != y || pos.z != z) {
            // position updated
            Vector3f cpos = spatial.getLocalTranslation();
            sx = cpos.x; sy = cpos.y; sz = cpos.z;
            st = Time.value;

            ex = x = pos.x; ey = y = pos.y; ez = z = pos.z; 
            
            et = t2 = Time.value + (Time.value - t);
        }
        t = pos.t;
        
        
        apply(Time.value);
    }

    public boolean apply( long now ) {
        if( now >= t2) {
            Log.info("no more");
            // Force the spatial to the last position
            spatial.setLocalTranslation((float) ex, (float) ey, (float) ez);
            //spatial.setLocalRotation(endRot);

            return false; // no more to go
        } else {
            // Interpolate... guaranteed to have a non-zero time delta here
            double part = (double) (now - st) / (double) (et - st);                
            Log.info("interpolate: " + part);

            // Do our own interp calculation because Vector3f's is inaccurate and
            // can return values out of range... especially in cases where part is
            // small and delta between coordinates is 0.  (Though this probably
            // wasn't the issue I was trying to fix, it is worrying in general.)                
            double x = sx + (ex - sx) * part;
            double y = sy + (ey - sy) * part;
            double z = sz + (ez - sz) * part;
            spatial.setLocalTranslation((float)x, (float)y, (float)z);

            //Quaternion rot = startRot.clone();
            //rot.nlerp(endRot, (float)part);
            //spatial.setLocalRotation(rot);

            return true; // still have more to go
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
