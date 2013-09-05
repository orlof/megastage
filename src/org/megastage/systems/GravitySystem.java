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
    @Mapper ComponentMapper<Velocity> velocityMapper;
    @Mapper ComponentMapper<Physical> physicalMapper;
    @Mapper ComponentMapper<Position> positionMapper;

    private ImmutableBag<Entity> groupGravityWell;

    public GravitySystem() {
        super(Aspect.getAspectForAll(Velocity.class, Physical.class));
    }

    @Override
    public void initialize() {
        groupGravityWell = world.getManager(GroupManager.class).getEntities("gravity well");
    }

    @Override
    protected void process(Entity entity) {
        Position pos = positionMapper.get(entity);
        Velocity vel = velocityMapper.get(entity);
        double ax = 0.0d, ay = 0.0d, az = 0.0d;
        
        for(int i=0; i < groupGravityWell.size(); i++) {
            Entity gw = groupGravityWell.get(i);
            Position gwPos = positionMapper.get(gw);
            double dx = gwPos.x - pos.x;
            double dy = gwPos.y - pos.y;
            double dz = gwPos.z - pos.z;
            
            Physical gwPhy = physicalMapper.get(gw);
            double distanceSquared = dx*dx + dy*dy + dz*dz;
            double dv = 1000.0d * world.delta * Globals.G * gwPhy.mass / distanceSquared;

            double distance = Math.sqrt(distanceSquared);
            vel.x += dv * dx / distance;
            vel.y += dv * dy / distance;
            vel.z += dv * dz / distance;
        }
    }
}
