package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.Mass;
import org.megastage.components.Orbit;
import org.megastage.components.Position;
import org.megastage.util.Globals;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrbitalMovementSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Position> POSITION;
    @Mapper ComponentMapper<Orbit> ORBIT;
    @Mapper ComponentMapper<Mass> MASS;

    public OrbitalMovementSystem() {
        super(Aspect.getAspectForAll(Position.class, Orbit.class));
    }

    @Override
    protected void process(Entity entity) {
        double time = ServerGlobals.time / 1000.0d;
        
        Orbit orbit = ORBIT.get(entity);
        Entity center = world.getEntity(orbit.center);
        
        Mass centerMass = MASS.get(center);        
        Vector localSum = orbit.getLocalCoordinates(time, centerMass.mass);
        
        while(!isOrbitAroundFixedPosition(center)) {
            orbit = ORBIT.get(center);
            center = world.getEntity(orbit.center);
            centerMass = MASS.get(center);        
            localSum = localSum.add(orbit.getLocalCoordinates(time, centerMass.mass));
        }

        Position fixedStar = POSITION.get(center);

        Position position = POSITION.get(entity);
        position.x = Math.round(localSum.x) + fixedStar.x;
        position.y = fixedStar.y;
        position.z = Math.round(localSum.z) + fixedStar.z;
        
    }

    private boolean isOrbitAroundFixedPosition(Entity center) {
        return !ORBIT.has(center);
    }
}
