package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.LocalPosition;
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
public class OrbitalMovementSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Position> POSITION;
    @Mapper ComponentMapper<Orbit> ORBIT;

    public OrbitalMovementSystem() {
        super(Aspect.getAspectForAll(Position.class, Orbit.class));
    }

    @Override
    protected void process(Entity entity) {
        double time = Globals.time / 1000.0d;
        
        Orbit orbit = ORBIT.get(entity);
        Vector localSum = orbit.getLocalCoordinates(time);
        
        while(!isOrbitAroundFixedPosition(orbit)) {
            orbit = getCentersOrbit(orbit);
            localSum = localSum.add(orbit.getLocalCoordinates(time));
        }

        Position fixedStar = getCentersFixedPosition(orbit);

        Position position = POSITION.get(entity);
        position.x = Math.round(localSum.x) + fixedStar.x;
        position.y = Math.round(localSum.y) + fixedStar.y;
        position.y = fixedStar.z;
    }

    private Position getCentersFixedPosition(Orbit orbit) {
        return POSITION.get(orbit.center);
    }

    private Orbit getCentersOrbit(Orbit orbit) {
        return ORBIT.get(orbit.center);
    }

    private boolean isOrbitAroundFixedPosition(Orbit orbit) {
        return !ORBIT.has(orbit.center);
    }
}
