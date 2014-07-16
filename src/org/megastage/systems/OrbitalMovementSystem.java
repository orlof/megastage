package org.megastage.systems;

import com.jme3.math.Vector3f;
import org.megastage.components.Orbit;
import org.megastage.components.Position;
import org.megastage.components.Velocity;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;

public class OrbitalMovementSystem extends Processor {
    public OrbitalMovementSystem(World world, long interval) {
        super(world, interval, CompType.Position, CompType.Orbit);
    }

    @Override
    protected void process(int eid) {
        float secs = world.time / 1000.0f;
        
        Orbit orbit = (Orbit) world.getComponent(eid, CompType.Orbit);
        
        Vector3f localSum = orbit.getLocalCoordinates(secs);
        
        while(isInOrbit(orbit.center)) {
            orbit = (Orbit) world.getComponent(orbit.center, CompType.Orbit);
            localSum = localSum.add(orbit.getLocalCoordinates(secs));
        }

        Position fixedStar = (Position) world.getComponent(orbit.center, CompType.Position);
        float x = Math.round(1000 * localSum.x) + fixedStar.coords.x;
        float y = fixedStar.coords.y;
        float z = Math.round(1000* localSum.z) + fixedStar.coords.z;

        Position position = (Position) world.getComponent(eid, CompType.Position);
        Velocity velocity = (Velocity) world.getComponent(eid, CompType.Velocity);
        velocity.vector = new Vector3f(x - position.coords.x, y - position.coords.y, z - position.coords.z);
        
        position.coords = new Vector3f(x, y, z);
        //position.dirty = true;
    }

    private boolean isInOrbit(int eid) {
        return world.hasComponent(eid, CompType.Orbit);
    }
}
