package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.dcpu.VirtualEngine;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.util.Mapper;
import org.megastage.util.GlobalTime;

public class ThermalLaserTestSystem extends EntitySystem {
    private long interval;
    private long acc;
    
    private int state = 0;
    
    public ThermalLaserTestSystem(long interval) {
        super(Aspect.getAspectForAll(VirtualThermalLaser.class));
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        if(GlobalTime.value >= acc) {
                acc = GlobalTime.value + interval;
                return true;
        }
        return false;
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        for(Entity entity: entities) {
            VirtualThermalLaser vtl = Mapper.VIRTUAL_THERMAL_LASER.get(entity);
            if(vtl.status == VirtualThermalLaser.STATUS_DORMANT) {
                vtl.wattage = 1000;
                vtl.fireWeapon((char) 10);
            }
        }
    }	
}
