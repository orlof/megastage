package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Position;
import org.megastage.components.dcpu.VirtualForceField;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.server.TargetManager;
import org.megastage.server.TargetManager.ForceFieldHit;
import org.megastage.server.TargetManager.Hit;
import org.megastage.server.TargetManager.ShipStructureHit;
import org.megastage.server.TargetManager.Target;
import org.megastage.util.ID;
import org.megastage.util.Mapper;
import org.megastage.util.Quaternion;
import org.megastage.util.Time;
import org.megastage.util.Vector3d;

public class ThermalLaserSystem extends SystemTemplate {
    public ThermalLaserSystem() {
        super(Aspect.getAspectForAll(VirtualThermalLaser.class));
    }

    @Override
    protected void process(Entity vtlEntity) {
        VirtualThermalLaser vtlComponent = Mapper.VIRTUAL_THERMAL_LASER.get(vtlEntity);
        switch(vtlComponent.status) {
            case VirtualThermalLaser.STATUS_DORMANT:
                break;
            case VirtualThermalLaser.STATUS_FIRING:
                if(Time.value < vtlComponent.startTime + vtlComponent.duration) {
                    // firing
                    Hit hit = fireWeapon(vtlEntity, vtlComponent);
                    Log.info(hit.toString());
                    
                    if(hit == TargetManager.NO_HIT) {
                        break;
                    } else if(hit instanceof ForceFieldHit) {
                        ForceFieldHit ffhit = (ForceFieldHit) hit;
                        
                        VirtualForceField forceField = Mapper.VIRTUAL_FORCE_FIELD.get(ffhit.entity);
                        forceField.damage(world.getDelta() * vtlComponent.wattage);

                    } else if(hit instanceof ShipStructureHit) {
                        ShipStructureHit shit = (ShipStructureHit) hit;
                        
                    } else {
                        Log.error("Unknown hit target: " + hit.toString());
                    }
                    
                } else {
                    // turn off
                    vtlComponent.startTime = Time.value;
                    vtlComponent.duration = vtlComponent.duration * vtlComponent.wattage / 20;
                    vtlComponent.status = VirtualThermalLaser.STATUS_COOLDOWN;
                    vtlComponent.dirty = true;
                }
                break;
            case VirtualThermalLaser.STATUS_COOLDOWN:
                if(Time.value >= vtlComponent.startTime + vtlComponent.duration) {
                    vtlComponent.status = VirtualThermalLaser.STATUS_DORMANT;
                    vtlComponent.dirty = true;
                }
                break;
        }
    }

    public static final Vector3d FORWARD_VECTOR = new Vector3d(0,0,-1);
    
    public Hit fireWeapon(Entity vtlEntity, VirtualThermalLaser vtlComponent) {
        return TargetManager.findHit(vtlEntity, vtlComponent);
    }
}
