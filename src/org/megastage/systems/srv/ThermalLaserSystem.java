package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.util.Log;
import java.util.Random;
import org.megastage.components.BlockChange;
import org.megastage.components.dcpu.VirtualForceField;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.server.ForceFieldHit;
import org.megastage.server.Hit;
import org.megastage.server.NoHit;
import org.megastage.server.ShipStructureHit;
import org.megastage.util.Vector3d;

public class ThermalLaserSystem extends Processor {
    Random random = new Random();

    public ThermalLaserSystem(World world, long interval) {
        super(world, interval, CompType.VirtualThermalLaser);
    }

    @Override
    protected void process(int eid) {
        VirtualThermalLaser vtlComponent = (VirtualThermalLaser) world.getComponent(eid, CompType.VirtualThermalLaser);
        switch(vtlComponent.status) {
            case VirtualThermalLaser.STATUS_DORMANT:
                break;
            case VirtualThermalLaser.STATUS_FIRING:
                if(world.time < vtlComponent.startTime + vtlComponent.duration) {
                    // firing
                    Hit hit = TargetManagerSystem.INSTANCE.findHit(eid, vtlComponent);
                    
                    if(hit instanceof NoHit) {
                        vtlComponent.setHit(0f);
                        break;
                    } else if(hit instanceof ForceFieldHit) {
                        ForceFieldHit ffhit = (ForceFieldHit) hit;
                        
                        VirtualForceField forceField = (VirtualForceField) world.getComponent(ffhit.eid, CompType.VirtualForceField);
                        forceField.damage(ffhit.eid, world.delta * vtlComponent.wattage);

                        vtlComponent.setHit((float) hit.distance);

                    } else if(hit instanceof ShipStructureHit) {
                        ShipStructureHit shit = (ShipStructureHit) hit;
                        ShipGeometry geom = (ShipGeometry) world.getComponent(shit.eid, CompType.ShipGeometry);
                        
                        vtlComponent.setHit((float) hit.distance);

                        double shotPower = world.delta * vtlComponent.wattage;
                        if(shotPower > 50.0 * random.nextDouble()) {
                            geom.map.set(shit.block.getX(), shit.block.getY(), shit.block.getZ(), (char) 0);
                        }
                        
                    } else {
                        Log.error("Unknown hit target: " + hit.toString());
                    }
                    
                } else {
                    // turn off
                    vtlComponent.setStatusCooldown(world.time);
                }
                break;
            case VirtualThermalLaser.STATUS_COOLDOWN:
                if(world.time >= vtlComponent.startTime + vtlComponent.duration) {
                    vtlComponent.status = VirtualThermalLaser.STATUS_DORMANT;
                    vtlComponent.setDirty(true);
                }
                break;
        }
    }

    public static final Vector3d FORWARD_VECTOR = new Vector3d(0,0,-1);
}
