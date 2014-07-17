package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.Vector3f;
import org.megastage.components.*;
import org.megastage.components.srv.Acceleration;
import org.megastage.components.Velocity;
import org.megastage.ecs.CompType;

public class ShipMovementSystem extends Processor {
    public ShipMovementSystem(World world, long interval) {
        super(world, interval, CompType.Position, CompType.Velocity, CompType.Acceleration);
    }

    @Override
    protected void process(int eid) {
        Velocity velocity = (Velocity) world.getComponent(eid, CompType.Velocity);
        Acceleration acceleration = (Acceleration) world.getComponent(eid, CompType.Acceleration);
        Position position = (Position) world.getComponent(eid, CompType.Position);

        if(Log.TRACE) {
            Log.info(String.format("[%d] %s / %s / %s", eid, position, velocity, acceleration));
        }
        
        position.add(velocity.vector.mult(world.delta / 2.0f));
        velocity.vector.addLocal(acceleration.vector.mult(world.delta));
        position.add(velocity.vector.mult(world.delta / 2.0f));
        
        acceleration.vector = Vector3f.ZERO;
    }
}