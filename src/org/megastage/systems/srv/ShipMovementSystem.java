package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.*;
import org.megastage.components.srv.Acceleration;
import org.megastage.components.srv.Velocity;
import org.megastage.util.Vector3d;

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

        Log.trace(entity.toString() + acceleration.toString());
        Log.trace(entity.toString() + velocity.toString());
        Log.trace(entity.toString() + position.toString());
        
        position.move(velocity, world.delta / 2.0f);
        velocity.accelerate(acceleration, world.delta);
        position.move(velocity, world.delta / 2.0f);

        Log.trace(entity.toString() + velocity.toString());

        acceleration.set(Vector3d.ZERO);
    }
}