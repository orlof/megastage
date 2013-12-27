package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
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
    @Mapper ComponentMapper<Position> positionMapper;
    @Mapper ComponentMapper<Velocity> velocityMapper;
    @Mapper ComponentMapper<Acceleration> accelerationMapper;

    public ShipMovementSystem() {
        super(Aspect.getAspectForAll(Position.class, Velocity.class, Acceleration.class));
    }

    @Override
    protected void process(Entity entity) {
        Velocity velocity = velocityMapper.get(entity);
        Acceleration acceleration = accelerationMapper.get(entity);
        Position position = positionMapper.get(entity);

        Log.debug(entity.toString() + acceleration.toString());
        
        position.move(velocity, world.delta / 2.0f);
        velocity.accelerate(acceleration, world.delta);
        position.move(velocity, world.delta / 2.0f);

        Log.debug(entity.toString() + velocity.toString());

        acceleration.set(Vector.ZERO);
    }
}
