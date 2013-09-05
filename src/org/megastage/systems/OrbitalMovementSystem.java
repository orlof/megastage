package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.util.Globals;
import org.megastage.components.LocalPosition;
import org.megastage.components.Orbit;
import org.megastage.components.Physical;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrbitalMovementSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Orbit> orbitMapper;
    @Mapper ComponentMapper<LocalPosition> localPositionMapper;

    public OrbitalMovementSystem() {
        super(Aspect.getAspectForAll(Orbit.class, LocalPosition.class));
    }

    @Override
    protected void process(Entity entity) {
        Orbit orbit = orbitMapper.get(entity);

        double mass = orbit.center.getComponent(Physical.class).mass;
        double period = 2.0d * Math.PI * Math.sqrt(Math.pow(orbit.distance, 3.0d) / (Globals.G * mass));

        double angularSpeed = 2.0d * Math.PI / period;
        double angle = angularSpeed * Globals.time;

        LocalPosition locPos = localPositionMapper.get(entity);
        locPos.parent = orbit.center;
        locPos.x = (long) (orbit.distance * Math.sin(angle));
        locPos.y = (long) (orbit.distance * Math.cos(angle));
    }
}
