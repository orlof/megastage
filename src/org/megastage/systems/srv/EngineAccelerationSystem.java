package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.srv.Acceleration;
import org.megastage.components.Mass;
import org.megastage.components.Rotation;
import org.megastage.components.dcpu.VirtualEngine;
import org.megastage.util.ID;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

public class EngineAccelerationSystem extends EntityProcessingSystem {
    ComponentMapper<VirtualEngine> ENGINE;
    ComponentMapper<Rotation> ROTATION;
    ComponentMapper<Mass> MASS;
    ComponentMapper<Acceleration> ACCELERATION;

    public EngineAccelerationSystem() {
        super(Aspect.getAspectForAll(VirtualEngine.class));
    }

    @Override
    public void initialize() {
        ENGINE = world.getMapper(VirtualEngine.class);
        ROTATION = world.getMapper(Rotation.class);
        MASS = world.getMapper(Mass.class);
        ACCELERATION = world.getMapper(Acceleration.class);
    }

    @Override
    protected void process(Entity entity) {
        VirtualEngine engine = ENGINE.get(entity);

        if(engine.isActive()) {
            Mass mass = MASS.get(engine.ship);
            if(mass == null) return;

            double shipMass = mass.mass;
            Vector3d acc = engine.getAcceleration(shipMass);
            
            // rotate acceleration into global coordinate system
            Rotation rotation = ROTATION.get(engine.ship);
            if(rotation != null) {
                Quaternion shipRot = rotation.getQuaternion();
                acc = acc.multiply(shipRot);
            }

            Log.trace(ID.get(entity) + acc.toString());
            
            ACCELERATION.get(engine.ship).add(acc);
        }
    }
}
