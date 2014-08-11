package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.components.srv.VectorAttack;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.Processor;

public class ThermalLaserSystem extends Processor {
    public ThermalLaserSystem(World world, long interval) {
        super(world, interval, CompType.VirtualThermalLaser);
    }

    @Override
    protected void process(int eid) throws ECSException {
        VirtualThermalLaser vtlComponent = (VirtualThermalLaser) world.getComponent(eid, CompType.VirtualThermalLaser);
        switch(vtlComponent.status) {
            case FIRING:
                VectorAttack att = (VectorAttack) world.getComponentOrError(eid, CompType.VectorAttack);
                if(world.time < vtlComponent.beamEndTime) {
                    att.damageRate = vtlComponent.getDamageRate();
                    att.enabled = true;
                } else {
                    // turn off
                    vtlComponent.status = VirtualThermalLaser.Status.IDLE;
                    att.enabled = false;
                }
                break;
        }
    }
}
