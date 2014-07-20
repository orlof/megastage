package org.megastage.client.controls;

import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import org.megastage.client.ClientGlobals;
import org.megastage.client.SoundManager;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.components.transfer.ThermalLaserData;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class ThermalLaserControl extends AbstractControl {
    private final int eid;
    private final AudioNode an;
    private char status = 0xffff;
    private float length;

    public ThermalLaserControl(int eid) {
        this.eid = eid; 
        this.an = SoundManager.get(SoundManager.LASER_BEAM).clone();
        this.an.setLooping(true);
    }

    private static final Vector3f FORWARD = new Vector3f(0,0,-1);
    
    @Override
    protected void controlUpdate(float tpf) {
        ThermalLaserData data = (ThermalLaserData) World.INSTANCE.getComponent(eid, CompType.ThermalLaserData);
        
        if(data == null) {
            spatial.setCullHint(Spatial.CullHint.Always);
        } else {
            if(status != data.status) {
                status = data.status;
                //Log.info(ID.get(entity) + "status=" + (int) status);

                switch(data.status) {
                    case VirtualThermalLaser.STATUS_FIRING:
                        an.play();
                        spatial.setCullHint(Spatial.CullHint.Inherit);
                        break;
                    case VirtualThermalLaser.STATUS_COOLDOWN:
                        an.pause();
                        spatial.setCullHint(Spatial.CullHint.Always);
                        break;
                    case VirtualThermalLaser.STATUS_DORMANT:
                        an.pause();
                        spatial.setCullHint(Spatial.CullHint.Always);
                        break;
                }
            }

            float distance = data.range;
            
            if(status == VirtualThermalLaser.STATUS_FIRING) {
                Vector3f worldTranslation = spatial.getParent().getWorldTranslation();
                Vector3f worldDirection = spatial.getParent().getWorldRotation().mult(FORWARD);
                //Vector3f contactPoint = worldTranslation.add(worldDirection.mult(data.range));
                //worldTranslation = worldTranslation.add(worldDirection.mult(2.6f));

                final CollisionResults crs = new CollisionResults();
                ClientGlobals.rootNode.collideWith(new Ray(worldTranslation, worldDirection), crs);

                for(CollisionResult cr: crs) {
                    Geometry geom = cr.getGeometry();
                    if(geom.getCullHint() == Spatial.CullHint.Always) {
                        continue;
                    }
                    
                    if(geom.hasAncestor(spatial.getParent())) {
                        continue;
                    }
                    
                    if(geom.getName().equals("forceshield")) {
                        float dist = spatial.getParent().getWorldTranslation().distance(geom.getWorldTranslation());
                        //Log.info(spatial.getParent().getWorldTranslation().toString());
                        //Log.info(geom.getWorldTranslation().toString());
                        float rad = ((Sphere) geom.getMesh()).getRadius();
                        //Log.info(ID.get(entity) + dist + " < " + rad);
                        if(dist < rad) {
                            continue;
                        }
                    }
                    
                    if(cr.getDistance() < distance) {
                        //distance = cr.getDistance();
                    }

                    ForceFieldControl control = cr.getGeometry().getControl(ForceFieldControl.class);
                    
                    if(control != null) {
                        control.registerHit(cr.getContactPoint(), eid);
                    }
                    
                    break;
                }
            }                        

            Cylinder cylinder = (Cylinder) ((Geometry) spatial).getMesh();
            if(distance != cylinder.getHeight()) {
                cylinder.updateGeometry(8, 8, 0.2f, 0.2f, distance, true, false);
                spatial.setLocalTranslation(0, 0, -distance/2f);
            }
                    
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
