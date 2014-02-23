package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.util.Mapper;
import org.megastage.util.Time;

public class ThermalLaserSystem extends SystemTemplate {
    public ThermalLaserSystem() {
        super(Aspect.getAspectForAll(VirtualThermalLaser.class));
    }

    @Override
    protected void process(Entity e) {
        VirtualThermalLaser vtl = Mapper.VIRTUAL_THERMAL_LASER.get(e);
        switch(vtl.status) {
            case VirtualThermalLaser.STATUS_DORMANT:
                break;
            case VirtualThermalLaser.STATUS_FIRING:
                if(Time.value >= vtl.startTime + vtl. duration) {
                    vtl.startTime = Time.value;
                    vtl.duration = vtl.duration * vtl.wattage / 100;
                    vtl.status = VirtualThermalLaser.STATUS_COOLDOWN;
                    vtl.dirty = true;
                }
                break;
            case VirtualThermalLaser.STATUS_COOLDOWN:
                if(Time.value >= vtl.startTime + vtl. duration) {
                    vtl.status = VirtualThermalLaser.STATUS_DORMANT;
                    vtl.dirty = true;
                }
                break;
        }
    }
}
