package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.*;
import org.megastage.components.srv.Acceleration;
import org.megastage.components.Velocity;
import org.megastage.util.Vector3d;

public class ShipMovementSystem extends EntityProcessingSystem {
    ComponentMapper<Position> POSITION;
    ComponentMapper<Velocity> VELOCITY;
    ComponentMapper<Acceleration> ACCELERATION;

    public ShipMovementSystem() {
        super(Aspect.getAspectForAll(Position.class, Velocity.class, Acceleration.class));
    }

    @Override
    public void initialize() {
        POSITION = world.getMapper(Position.class);
        VELOCITY = world.getMapper(Velocity.class);
        ACCELERATION = world.getMapper(Acceleration.class);
    }

    @Override
    protected void process(Entity entity) {
        Velocity velocity = VELOCITY.get(entity);
        Acceleration acceleration = ACCELERATION.get(entity);
        Position position = POSITION.get(entity);

        if(Log.TRACE) {
            Log.info(entity.toString() + acceleration.toString());
            Log.info(entity.toString() + velocity.toString());
            Log.info(entity.toString() + position.toString());
        }
        
        position.move(velocity, world.getDelta() / 2.0f);
        velocity.accelerate(acceleration, world.getDelta());
        position.move(velocity, world.getDelta() / 2.0f);

        acceleration.set(Vector3d.ZERO);
    }
}