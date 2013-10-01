package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.LocalAcceleration;
import org.megastage.components.Physical;
import org.megastage.components.dcpu.HWEngineController;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class EngineAccelerationSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<HWEngineController> engineMapper;
    @Mapper ComponentMapper<Physical> physicalMapper;
    @Mapper ComponentMapper<LocalAcceleration> localAccelerationMapper;

    public EngineAccelerationSystem() {
        super(Aspect.getAspectForAll(HWEngineController.class));
    }

    @Override
    protected void process(Entity entity) {
        HWEngineController engine = engineMapper.get(entity);

        if(engine.isActive()) {
            double shipMass = physicalMapper.get(engine.ship).mass;
            Vector acceleration = engine.getCurrentAccelerationVector(shipMass);
            localAccelerationMapper.get(engine.ship).add(acceleration);
        }
    }
}
