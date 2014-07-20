package org.megastage.systems.srv;

import com.jme3.math.Vector3f;
import org.megastage.components.srv.Acceleration;
import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.ecs.CompType;

public class GravityAccelerationSystem extends Processor {
    public GravityAccelerationSystem(World world, long interval) {
        super(world, interval, CompType.AffectedByGravityFlag, CompType.Acceleration, CompType.Position);
    }

    @Override
    protected void process(int eid) {
        Vector3f gravityField = GravityManagerSystem.INSTANCE.getGravitationalAcceleration(eid);
        Acceleration acceleration = (Acceleration) world.getComponent(eid, CompType.Acceleration);
        acceleration.jerk(gravityField);
    }
}
