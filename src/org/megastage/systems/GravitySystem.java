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

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class GravitySystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Acceleration> accelerationMapper;
    @Mapper ComponentMapper<Physical> physicalMapper;
    @Mapper ComponentMapper<Position> positionMapper;

    private ImmutableBag<Entity> celestialBodies;

    public GravitySystem() {
        super(Aspect.getAspectForAll(Acceleration.class, Position.class));
    }

    @Override
    public void initialize() {
        celestialBodies = world.getManager(GroupManager.class).getEntities("gravity well");
    }

    @Override
    protected void process(Entity entity) {
        Position position = positionMapper.get(entity);
        Acceleration acceleration = accelerationMapper.get(entity);

        for(int i=0; i < celestialBodies.size(); i++) {
            Entity celestialEntity = celestialBodies.get(i);
            Position celestialPosition = positionMapper.get(celestialEntity);
            double dx = celestialPosition.x - position.x;
            double dy = celestialPosition.y - position.y;
            double dz = celestialPosition.z - position.z;

            double distanceSquared = dx*dx + dy*dy + dz*dz;

            double celestialMass = physicalMapper.get(celestialEntity).mass;
            double gravitationalField = Globals.G * celestialMass / distanceSquared;

            double distance = Math.sqrt(distanceSquared);

            double multiplier = gravitationalField / distance;
            acceleration.add(multiplier * dx, multiplier * dy, multiplier * dz);
        }
    }
}
