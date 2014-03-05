package org.megastage.systems.srv;

import org.megastage.components.srv.Acceleration;
import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.*;
import org.megastage.components.srv.AffectedByGravityFlag;
import org.megastage.server.GravityManager;
import org.megastage.util.Mapper;
import org.megastage.util.Vector3d;

public class GravityAccelerationSystem extends EntityProcessingSystem {
    public GravityAccelerationSystem() {
        super(Aspect.getAspectForAll(AffectedByGravityFlag.class, Acceleration.class, Position.class));
    }

    @Override
    protected void process(Entity entity) {
        Vector3d gravityField = GravityManager.getGravitationalAcceleration(entity);
        Mapper.ACCELERATION.get(entity).add(gravityField);
    }
}
