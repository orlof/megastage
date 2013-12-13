package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import org.megastage.util.Globals;
import org.megastage.components.*;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class GravityAccelerationSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Acceleration> ACCELERATION;
    @Mapper ComponentMapper<Position> POSITION;

    private GravityFieldSystem gravityFieldSystem;

    public GravityAccelerationSystem() {
        super(Aspect.getAspectForAll(GravityAcceleration.class, Position.class));
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
        acceleration.add(gravityField);
    }
}
