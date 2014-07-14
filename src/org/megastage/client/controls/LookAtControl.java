package org.megastage.client.controls;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import org.megastage.client.SpatialManager;
import org.megastage.components.transfer.RadarTargetData;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class LookAtControl extends AbstractControl {
    private final int eid;
    
    public LookAtControl(int eid) {
         this.eid = eid; 
    }

    @Override
    protected void controlUpdate(float tpf) {
        RadarTargetData data = (RadarTargetData) World.INSTANCE.getComponent(eid, CompType.RadarTargetData);
        if(data == null || data.eid == 0) return;

        Node tn = SpatialManager.getOrCreateNode(data.eid);
        
        spatial.lookAt(tn.getWorldTranslation(), Vector3f.UNIT_Y.clone());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
