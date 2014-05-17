package org.megastage.systems;

import com.esotericsoftware.minlog.Log;
import org.megastage.components.Orbit;
import org.megastage.components.Position;
import org.megastage.components.Velocity;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.util.ID;
import org.megastage.util.Vector3d;

public class OrbitalMovementSystem extends Processor {
    public OrbitalMovementSystem(World world, long interval) {
        super(world, interval, CompType.Position, CompType.Orbit);
    }

    @Override
    protected void process(int eid) {
        double secs = world.time / 1000.0;
        
        Orbit orbit = (Orbit) world.getComponent(eid, CompType.Orbit);
        
        Vector3d localSum = orbit.getLocalCoordinates(secs);
        
        while(isInOrbit(orbit.center)) {
            orbit = (Orbit) world.getComponent(orbit.center, CompType.Orbit);
            localSum = localSum.add(orbit.getLocalCoordinates(secs));
        }

        Position fixedStar = (Position) world.getComponent(orbit.center, CompType.Position);
        long x = Math.round(1000 * localSum.x) + fixedStar.x;
        long y = fixedStar.y;
        long z = Math.round(1000* localSum.z) + fixedStar.z;

        Position position = (Position) world.getComponent(eid, CompType.Position);
        Velocity velocity = (Velocity) world.getComponent(eid, CompType.Velocity);
        velocity.vector = new Vector3d(x - position.x, y - position.y, z - position.z);
        
        position.set(x, y, z);
        //position.dirty = true;
    }

    private boolean isInOrbit(int eid) {
        return world.hasComponent(eid, CompType.Orbit);
    }
}
