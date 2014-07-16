package org.megastage.systems.srv;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.components.Mass;
import org.megastage.components.Rotation;
import org.megastage.components.dcpu.VirtualEngine;
import org.megastage.components.srv.Acceleration;
import org.megastage.ecs.CompType;

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

            float shipMass = mass.mass;
            Vector3f acc = engine.getAcceleration(shipMass);
            
            // rotate acceleration into global coordinate system
            Rotation rotation = (Rotation) world.getComponent(engine.shipEID, CompType.Rotation);
            if(rotation != null) {
                Quaternion shipRot = rotation.value;
                shipRot.multLocal(acc);
            }

            Acceleration acceleration = (Acceleration) world.getComponent(engine.shipEID, CompType.Acceleration);
            acceleration.vector.addLocal(acc);
        }
    }
}
