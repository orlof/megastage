package org.megastage.systems.srv;

import org.megastage.components.srv.Acceleration;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.*;
import org.megastage.util.Vector3d;

public class GravityAccelerationSystem extends EntityProcessingSystem {
    ComponentMapper<Acceleration> ACCELERATION;
    ComponentMapper<Position> POSITION;

    private GravityFieldSystem gravityFieldSystem;

    public GravityAccelerationSystem() {
        super(Aspect.getAspectForAll(Acceleration.class, Position.class));
    }

    @Override
    public void initialize() {
        ACCELERATION = world.getMapper(Acceleration.class);
        POSITION = world.getMapper(Position.class);

        gravityFieldSystem = world.getSystem(GravityFieldSystem.class);
    }

    @Override
    protected void process(Entity entity) {
        Position position = POSITION.get(entity);
        Acceleration acceleration = ACCELERATION.get(entity);

        Vector3d gravityField = gravityFieldSystem.getGravityField(position);
        acceleration.add(gravityField);
    }
}
