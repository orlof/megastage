package org.megastage.client.controls;

import com.esotericsoftware.minlog.Log;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;
import org.megastage.client.ClientGlobals;
import org.megastage.components.PositionOffset;
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
        Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
        if(pos == null) {
            Log.warn("no position component for " + eid);
            return;
        }

        Vector3f localPos = pos.getVector3f();
        
        if(spatial.getParent() == ClientGlobals.globalRotationNode) {
            // calculate position relative to player's ship
            Position origoPos = (Position) World.INSTANCE.getComponent(ClientGlobals.playerParentEntity, CompType.Position);
            if(origoPos != null) {
                localPos.subtractLocal(origoPos.getVector3f());
            }

            // calculate depth scaling (logarithmic if distance > 20000)
            float distance = localPos.length();
            float depth = depth(distance);

            float scale = depth / distance;
            localPos.multLocal(scale);
            spatial.scale(scale);
        }
        
        spatial.setLocalTranslation(localPos);
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
