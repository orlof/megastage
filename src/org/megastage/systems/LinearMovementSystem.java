package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.Position;
import org.megastage.components.Velocity;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinearMovementSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Velocity> vm;
    @Mapper ComponentMapper<Position> pm;

    public LinearMovementSystem() {
        super(Aspect.getAspectForAll(Position.class, Velocity.class));
    }

    @Override
    protected void process(Entity entity) {
        Position position = pm.get(entity);
        Velocity velocity = vm.get(entity);

        double multiplier = 1000.0d * world.delta;
        position.x += multiplier * velocity.x;
        position.y += multiplier * velocity.y;
        position.z += multiplier * velocity.z;
    }
}
