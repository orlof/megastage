package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.Mass;
import org.megastage.components.Rotation;
import org.megastage.components.dcpu.VirtualEngine;
import org.megastage.util.Mapper;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

public class EngineAccelerationSystem extends EntityProcessingSystem {
    public EngineAccelerationSystem() {
        super(Aspect.getAspectForAll(VirtualEngine.class));
    }

    @Override
    protected void process(Entity entity) {
        VirtualEngine engine = Mapper.VIRTUAL_ENGINE.get(entity);

        if(engine.isActive()) {
            Mass mass = Mapper.MASS.get(engine.ship);
            if(mass == null) return;

            double shipMass = mass.mass;
            Vector3d acc = engine.getAcceleration(shipMass);
            
            // rotate acceleration into global coordinate system
            Rotation rotation = Mapper.ROTATION.get(engine.ship);
            if(rotation != null) {
                Quaternion shipRot = rotation.getQuaternion4d();
                acc = acc.multiply(shipRot);
            }

            Mapper.ACCELERATION.get(engine.ship).add(acc);
        }
    }
}
