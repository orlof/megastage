package org.megastage.systems;

import org.megastage.components.srv.Acceleration;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.*;
import org.megastage.util.Vector;

public class GravityAccelerationSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Acceleration> ACCELERATION;
    @Mapper ComponentMapper<Position> POSITION;

    private GravityFieldSystem gravityFieldSystem;

    public GravityAccelerationSystem() {
        super(Aspect.getAspectForAll(Acceleration.class, Position.class));
    }

    @Override
    public void initialize() {
        gravityFieldSystem = world.getSystem(GravityFieldSystem.class);
    }

    @Override
    protected void process(Entity entity) {
        Position position = POSITION.get(entity);
        Acceleration acceleration = ACCELERATION.get(entity);

        Vector gravityField = gravityFieldSystem.getGravityField(position);
        Log.trace(entity.toString() + " in gravity field " + gravityField.toString());
        acceleration.add(gravityField);
    }
}
