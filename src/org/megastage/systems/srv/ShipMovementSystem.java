package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.*;
import org.megastage.components.srv.Acceleration;
import org.megastage.components.Velocity;
import org.megastage.ecs.CompType;
import org.megastage.util.Vector3d;

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
            Log.info(eid + acceleration.toString());
            Log.info(eid + velocity.toString());
            Log.info(eid + position.toString());
        }
        
        position.move(velocity, world.delta / 2.0f);
        velocity.accelerate(acceleration, world.delta);
        position.move(velocity, world.delta / 2.0f);

        acceleration.set(Vector3d.ZERO);
    }
}