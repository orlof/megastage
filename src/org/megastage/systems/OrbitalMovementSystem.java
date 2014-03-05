package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Mass;
import org.megastage.components.Orbit;
import org.megastage.components.Position;
import org.megastage.components.Velocity;
import org.megastage.util.ID;
import org.megastage.util.Time;
import org.megastage.util.Vector3d;

public class OrbitalMovementSystem extends EntityProcessingSystem {
    ComponentMapper<Position> POSITION;
    ComponentMapper<Velocity> VELOCITY;
    ComponentMapper<Orbit> ORBIT;
    ComponentMapper<Mass> MASS;

    public OrbitalMovementSystem() {
        super(Aspect.getAspectForAll(Position.class, Orbit.class));
    }

    @Override
    public void initialize() {
        ORBIT = world.getMapper(Orbit.class);
        POSITION = world.getMapper(Position.class);
        VELOCITY = world.getMapper(Velocity.class);
        MASS = world.getMapper(Mass.class);
    }

    @Override
    protected void process(Entity entity) {
        double time = Time.secs();
        
        Orbit orbit = ORBIT.get(entity);
        
        Entity center = world.getEntity(orbit.center);
        Vector3d localSum = orbit.getLocalCoordinates(time);
        
        while(!isInFixedPosition(center)) {
            orbit = ORBIT.get(center);
            center = world.getEntity(orbit.center);
            localSum = localSum.add(orbit.getLocalCoordinates(time));
        }

        Position fixedStar = POSITION.get(center);
        long x = Math.round(1000 * localSum.x) + fixedStar.x;
        long y = fixedStar.y;
        long z = Math.round(1000* localSum.z) + fixedStar.z;

        Position position = POSITION.get(entity);
        Velocity velocity = VELOCITY.get(entity);
        velocity.vector = new Vector3d(x - position.x, y - position.y, z - position.z);
        
        position.set(x, y, z);
        //position.dirty = true;
    }

    private boolean isInFixedPosition(Entity center) {
        return !ORBIT.has(center);
    }

}
