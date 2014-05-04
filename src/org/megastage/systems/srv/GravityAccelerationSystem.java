package org.megastage.systems.srv;

import org.megastage.components.srv.Acceleration;
import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.ecs.CompType;
import org.megastage.util.Vector3d;

public class GravityAccelerationSystem extends Processor {
    public GravityAccelerationSystem(World world, long interval) {
        super(world, interval, CompType.AffectedByGravityFlag, CompType.Acceleration, CompType.Position);
    }

    @Override
    protected void process(int eid) {
        Vector3d gravityField = GravityManagerSystem.INSTANCE.getGravitationalAcceleration(eid);
        Acceleration acceleration = (Acceleration) world.getComponent(eid, CompType.Acceleration);
        acceleration.add(gravityField);
    }
}
