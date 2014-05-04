package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.components.Mass;
import org.megastage.components.Rotation;
import org.megastage.components.dcpu.VirtualEngine;
import org.megastage.components.srv.Acceleration;
import org.megastage.ecs.CompType;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

public class EngineAccelerationSystem extends Processor {
    public EngineAccelerationSystem(World world, long interval) {
        super(world, interval, CompType.VirtualEngine);
    }

    @Override
    protected void process(int eid) {
        VirtualEngine engine = (VirtualEngine) world.getComponent(eid, CompType.VirtualEngine);

        if(engine.isActive()) {
            Mass mass = (Mass) world.getComponent(engine.shipEID, CompType.Mass);
            if(mass == null) return;

            double shipMass = mass.mass;
            Vector3d acc = engine.getAcceleration(shipMass);
            
            // rotate acceleration into global coordinate system
            Rotation rotation = (Rotation) world.getComponent(engine.shipEID, CompType.Rotation);
            if(rotation != null) {
                Quaternion shipRot = rotation.getQuaternion4d();
                acc = acc.multiply(shipRot);
            }

            Acceleration acceleration = (Acceleration) world.getComponent(engine.shipEID, CompType.Acceleration);
            acceleration.add(acc);
        }
    }
}
