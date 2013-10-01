package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.*;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShipMovementSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<LocalAcceleration> localAccelerationMapper;
    @Mapper ComponentMapper<GlobalAcceleration> globalAccelerationMapper;

    @Mapper ComponentMapper<Position> positionMapper;
    @Mapper ComponentMapper<Heading> headingMapper;
    @Mapper ComponentMapper<Velocity> shipVelocityMapper;

    public ShipMovementSystem() {
        super(Aspect.getAspectForAll(Position.class, LocalAcceleration.class, GlobalAcceleration.class));
    }

    @Override
    protected void process(Entity entity) {
        Velocity velocity = shipVelocityMapper.get(entity);

        // update velocity by gravitational effect
        GlobalAcceleration globalAcceleration = globalAccelerationMapper.get(entity);
        Vector velocityChange = globalAcceleration.getVelocityChange(world.delta);
        velocity.add(velocityChange);

        globalAcceleration.set(Vector.ZERO);

        // update velocity by ship's engine effect
        Quaternion shipHeading = headingMapper.get(entity).getGlobalRotation();

        LocalAcceleration localAcceleration = localAccelerationMapper.get(entity);
        velocityChange = localAcceleration.getVelocityChange(shipHeading, world.delta);
        velocity.add(velocityChange);

        localAcceleration.set(Vector.ZERO);

        // update position by velocity
        Position position = positionMapper.get(entity);
        position.add(velocity.vector);
    }
}
