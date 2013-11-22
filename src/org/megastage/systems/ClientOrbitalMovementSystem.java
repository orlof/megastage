package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Mass;
import org.megastage.components.Orbit;
import org.megastage.components.Position;
import org.megastage.util.Globals;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientOrbitalMovementSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Position> POSITION;
    @Mapper ComponentMapper<Orbit> ORBIT;
    @Mapper ComponentMapper<Mass> MASS;

    public ClientOrbitalMovementSystem() {
        super(Aspect.getAspectForAll(Position.class, Orbit.class));
    }

    @Override
    protected void process(Entity entity) {
        double time = Globals.time / 1000.0d;

        Orbit orbit = ORBIT.get(entity);
        Entity center = world.getEntity(orbit.center);
        
        Log.info(entity.toString() + " is orbiting around " + center.toString());
        
        Mass centerMass = MASS.get(center);        
        Vector localPosition = orbit.getLocalCoordinates(time, centerMass.mass);
        
        Log.info(entity.getId() + " <- Orbit"  + localPosition.toString());
        
        Position position = POSITION.get(entity);
        position.x = Math.round(localPosition.x);
        position.y = Math.round(localPosition.y);
        position.z = Math.round(localPosition.z);
        
        Log.info(entity.toString() + "Updated position " + position.toString());
    }
}
