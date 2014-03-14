package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.dcpu.VirtualPowerPlant;
import org.megastage.util.Mapper;

public class PowerSystem extends EntityProcessingSystem {
    public PowerSystem() {
        super(Aspect.getAspectForAll(VirtualPowerPlant.class));
    }

    @Override
    protected void process(Entity entity) {
        VirtualPowerPlant powerPlant = Mapper.VIRTUAL_POWER_PLANT.get(entity);
        powerPlant.resetFlux(world.getDelta());
    }
}
