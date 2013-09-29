package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.Acceleration;
import org.megastage.components.Position;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinearMovementSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Acceleration> accelerationMapper;
    @Mapper ComponentMapper<Position> positionMapper;

    public LinearMovementSystem() {
        super(Aspect.getAspectForAll(Position.class, Acceleration.class));
    }

    @Override
    protected void process(Entity entity) {
        Position position = positionMapper.get(entity);
        Acceleration acceleration = accelerationMapper.get(entity);

        double multiplier = 1000.0d * world.delta;
        position.x += multiplier * acceleration.getCoordinateX();
        position.y += multiplier * acceleration.getCoordinateY();
        position.z += multiplier * acceleration.getCoordinateZ();
    }
}
