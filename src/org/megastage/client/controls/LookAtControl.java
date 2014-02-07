package org.megastage.client.controls;

import com.artemis.Entity;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import org.megastage.client.ClientGlobals;
import org.megastage.components.Position;
import org.megastage.components.transfer.RadarTargetData;
import org.megastage.util.ID;

public class LookAtControl extends AbstractControl {
    private final Entity entity;
    
    public LookAtControl(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(entity == null) {
            return;
        }
        
        RadarTargetData rtd = entity.getComponent(RadarTargetData.class);
        if(rtd == null) return;

        Node tn = ClientGlobals.spatialManager.getNode(rtd.target);
        
        spatial.lookAt(tn.getWorldTranslation(), Vector3f.UNIT_Y.clone());
        //spatial.lookAt(new Vector3f(0,10000,0), Vector3f.UNIT_Y.clone());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
