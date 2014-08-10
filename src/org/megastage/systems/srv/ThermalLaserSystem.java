package org.megastage.systems.srv;

import com.jme3.math.Vector3f;
import org.megastage.ecs.World;
import java.util.Random;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.components.srv.VectorAttack;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;

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
                VectorAttack att = world.getOrCreateComponent(eid, CompType.VectorAttack, VectorAttack.class);
                if(world.time < vtlComponent.startTime + vtlComponent.duration) {
                    att.vector = new Vector3f(0, 0, -1);
                    att.damageRate = vtlComponent.wattage;
                    att.enabled = true;
                    
                } else {
                    // turn off
                    att.enabled = false;
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
}
