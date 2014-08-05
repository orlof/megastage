package org.megastage.client.controls;

import org.megastage.util.Log;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class PositionControl extends AbstractControl {
    public static final float VIEW_DIST_LINEAR = 20000.0f;
    public static final float VIEW_DIST_LOG = 20000.0f;
    public static final float REAL_DIST_FAR = 1.0e12f;
    public static final float K = VIEW_DIST_LOG / ((float) Math.log(REAL_DIST_FAR / VIEW_DIST_LINEAR));

    private final int eid;

    public PositionControl(int eid) {
        this.eid = eid; 
    }

    @Override
    protected void controlUpdate(float tpf) {
        // Log.info("%s in %s", spatial.getName(), spatial.getParent().getName());

        if(eid == ClientGlobals.baseEntity) {
            // position player's ship
            spatial.setLocalTranslation(Vector3f.ZERO);
            return;
        }
            
        Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
        if(pos == null) {
            Log.debug("No Position for " + eid);
            return;
        }

        Position origoPos = (Position) World.INSTANCE.getComponent(ClientGlobals.baseEntity, CompType.Position);
        if(origoPos == null) {
            Log.debug("No origo [%s] Position for [%s]", ClientGlobals.baseEntity, eid);
            return;
        }

        // calculate position relative to player's ship
        Vector3f localPos = pos.getCopy().subtractLocal(origoPos.get());

        // calculate depth scaling (logarithmic if distance > 20000)
        float distance = localPos.length();
        float depth = depth(distance);

        float scale = depth / distance;
        localPos.multLocal(scale);
        spatial.setLocalScale(scale);
        spatial.setLocalTranslation(localPos);

        // Log.info("distance: %f, scale: %f, coords:%s", distance, scale, localPos);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

    private float depth(float distance) {
        if(distance < VIEW_DIST_LINEAR) {
            return distance;
        } else {
            return VIEW_DIST_LINEAR + K * ((float) Math.log( distance / VIEW_DIST_LINEAR ));
        }
    }
}
