package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.Acceleration;
import org.megastage.components.ShipEngine;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccelerationZeroSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Acceleration> accelerationMapper;

    public AccelerationZeroSystem() {
        super(Aspect.getAspectForAll(Acceleration.class));
    }

    @Override
    protected void process(Entity entity) {
        accelerationMapper.get(entity).set(Vector.ZERO);
    }
}
