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
import org.megastage.util.Vector;
import com.esotericsoftware.minlog.Log;

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
        double time = Globals.time / 1000.0d;
        
        Orbit orbit = ORBIT.get(entity);
        Mass centerMass = MASS.get(orbit.center);        
        Vector localSum = orbit.getLocalCoordinates(time, centerMass.mass);
        
        while(!isOrbitAroundFixedPosition(orbit)) {
            orbit = getCentersOrbit(orbit);
            centerMass = MASS.get(orbit.center);        
            localSum = localSum.add(orbit.getLocalCoordinates(time, centerMass.mass));
        }

        Position fixedStar = getCentersFixedPosition(orbit);

        Position position = POSITION.get(entity);
        position.x = Math.round(localSum.x) + fixedStar.x;
        position.y = fixedStar.y;
        position.z = Math.round(localSum.z) + fixedStar.z;
        Log.info(position.toString());
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
