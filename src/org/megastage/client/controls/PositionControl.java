package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;
import org.megastage.client.ClientGlobals;
import org.megastage.util.ID;
import org.megastage.util.Mapper;
import org.megastage.util.Time;

/**
 *
 * @author Orlof
 */
public class PositionControl extends AbstractControl {
    private final Entity entity;
    private Position pos;
    private Interpolator interpolator = null;

    long lastUpdateTime;
    
    public PositionControl(Entity entity, boolean useInterpolator) {
        this.entity = entity;

        if(useInterpolator) {
            interpolator = new Interpolator();
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(pos == null) {
            pos = Mapper.POSITION.get(entity);
            if(pos == null) {
                return;
            }
        }

        if(ClientGlobals.shipEntity == entity) {
            spatial.setLocalTranslation(0,0,0);
            return;
        }

        if(interpolator == null) {
            spatial.setLocalTranslation(pos.getVector3f());
            return;
        }

        if(pos.dirty) {
            // position updated
            pos.dirty = false;

            Vector3f cpos = spatial.getLocalTranslation();
            Vector3f tpos = pos.getVector3f();
            
            long duration = 
                    (cpos.x == 0f && cpos.y == 0f && cpos.z == 0f) ? 
                    0: Time.value - lastUpdateTime; 
            if(duration > 100) duration = 100;
            
            lastUpdateTime = Time.value;

            interpolator.update(Time.value, Time.value + duration, cpos, tpos);
        }
        
        interpolator.apply();
    }

    private class Interpolator {
        private double sx,sy,sz,st;
        private double ex,ey,ez,et;
        private double dx,dy,dz,dt;
        
        private long startTime;
        private long endTime;
        
        void update(long startTime, long endTime, Vector3f start, Vector3f end) {
            sx=start.x; ex=end.x; dx=ex-sx;
            sy=start.y; ey=end.y; dy=ey-sy;
            sz=start.z; ez=end.z; dz=ez-sz;

            dt = endTime - startTime;
            st=startTime; et=endTime;
            
            this.startTime = startTime;
            this.endTime = endTime;

            if(Log.TRACE)
                Log.info(ID.get(entity) + start.toString() + "/" + (startTime % 100000) + ", " + end.toString() + "/" + (endTime % 100000));
        }

        public final void apply() {
            if( Time.value >= endTime) {
                spatial.setLocalTranslation((float) ex, (float) ey, (float) ez);
                return;
            } 
            
            if( Time.value <= startTime) {
                spatial.setLocalTranslation((float) sx, (float) sy, (float) sz);
                return;
            } 
            
            // Interpolate... guaranteed to have a non-zero time delta here
            double part = (Time.value - st) / dt;                

            double x = sx + dx * part;
            double y = sy + dy * part;
            double z = sz + dz * part;

            spatial.setLocalTranslation((float) x, (float) y, (float) z);
            Log.trace(x + ", " + y + ", " + z);
        }
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
