package org.megastage.client.controls;

import com.jme3.audio.AudioNode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Cylinder;
import org.megastage.client.EntityNode;
import org.megastage.client.SoundManager;
import org.megastage.client.SpatialManager;
import org.megastage.components.srv.VectorAttack;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.server.ForceFieldHit;
import org.megastage.server.NoHit;
import org.megastage.server.ShipStructureHit;

public class ThermalLaserControl extends AbstractControl {
    private final int eid;
    private final AudioNode an;

    private boolean firing = false;

    public ThermalLaserControl(int eid) {
        this.eid = eid; 
        this.an = SoundManager.get(SoundManager.LASER_BEAM).clone();
        this.an.setLooping(true);
    }

    @Override
    protected void controlUpdate(float tpf) {
        VectorAttack vecAtt = (VectorAttack) World.INSTANCE.getComponent(eid, CompType.VectorAttack);
        
        if(vecAtt == null) {
            spatial.setCullHint(Spatial.CullHint.Always);
            return;
        }

        if(firing != vecAtt.enabled) {
            firing = vecAtt.enabled;

            if(firing) {
                an.play();
                spatial.setCullHint(Spatial.CullHint.Inherit);
            } else {
                an.pause();
                spatial.setCullHint(Spatial.CullHint.Always);
            }
        }

        if(firing) {
            if(vecAtt.hit instanceof NoHit) {
            } else if(vecAtt.hit instanceof ForceFieldHit) {
                ForceFieldHit ffhit = (ForceFieldHit) vecAtt.hit;
                EntityNode forceField = SpatialManager.getOrCreateNode(eid);
                ForceFieldControl control = forceField.getControlRecursive(ForceFieldControl.class);

                if(control != null) {
                    control.registerHit(ffhit.contactPoint, ffhit.attacker);
                }
            } else if(vecAtt.hit instanceof ShipStructureHit) {
            }

            Cylinder cylinder = (Cylinder) ((Geometry) spatial).getMesh();
            if(vecAtt.hit.distance != cylinder.getHeight()) {
                cylinder.updateGeometry(8, 8, 0.2f, 0.2f, vecAtt.hit.distance, true, false);
                spatial.setLocalTranslation(0, 0, vecAtt.hit.distance / -2.0f);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
