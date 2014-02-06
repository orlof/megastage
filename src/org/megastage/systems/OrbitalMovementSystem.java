package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.Mass;
import org.megastage.components.Orbit;
import org.megastage.components.Position;
import org.megastage.util.Time;
import org.megastage.util.Vector3d;

public class OrbitalMovementSystem extends EntityProcessingSystem {
    ComponentMapper<Position> POSITION;
    ComponentMapper<Orbit> ORBIT;
    ComponentMapper<Mass> MASS;

    public OrbitalMovementSystem() {
        super(Aspect.getAspectForAll(Position.class, Orbit.class));
    }

    @Override
    public void initialize() {
        
        ORBIT = world.getMapper(Orbit.class);
        POSITION = world.getMapper(Position.class);
        MASS = world.getMapper(Mass.class);
    }

    @Override
    protected void process(Entity entity) {
        double time = Time.secs();
        
        Orbit orbit = ORBIT.get(entity);
        
        Entity center = world.getEntity(orbit.center);
        Mass centerMass = MASS.get(center);        
        Vector3d localSum = orbit.getLocalCoordinates(time, centerMass.mass);
        
        while(!isInFixedPosition(center)) {
            orbit = ORBIT.get(center);
            center = world.getEntity(orbit.center);
            centerMass = MASS.get(center);        
            localSum = localSum.add(orbit.getLocalCoordinates(time, centerMass.mass));
        }

        Position fixedStar = POSITION.get(center);

        Position position = POSITION.get(entity);
        position.x = Math.round(1000 * localSum.x) + fixedStar.x;
        position.y = fixedStar.y;
        position.z = Math.round(1000* localSum.z) + fixedStar.z;
        //Log.info(position.toString());
    }

    private boolean isInFixedPosition(Entity center) {
        return !ORBIT.has(center);
    }

}
