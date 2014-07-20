package org.megastage.systems.srv;

import com.jme3.math.Vector3f;
import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.components.Mass;
import org.megastage.components.Rotation;
import org.megastage.components.dcpu.VirtualEngine;
import org.megastage.components.srv.Acceleration;
import org.megastage.ecs.CompType;
import org.megastage.util.Log;

public class EngineAccelerationSystem extends Processor {
    public EngineAccelerationSystem(World world, long interval) {
        super(world, interval, CompType.VirtualEngine);
    }

    @Override
    protected void process(int eid) {
        VirtualEngine engine = (VirtualEngine) world.getComponent(eid, CompType.VirtualEngine);

        if(engine.isActive()) {
            Mass mass = (Mass) world.getComponent(engine.shipEID, CompType.Mass);
            Vector3f acc = engine.getForce().divideLocal(mass.value);
            
            // rotate acceleration into global coordinate system
            Rotation rotation = (Rotation) world.getComponent(engine.shipEID, CompType.Rotation);
            rotation.rotateLocal(acc);

            Acceleration acceleration = (Acceleration) world.getComponent(engine.shipEID, CompType.Acceleration);
            acceleration.jerk(acc);
        }
    }
}
