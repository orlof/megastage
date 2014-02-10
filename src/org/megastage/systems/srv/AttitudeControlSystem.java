package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.Rotation;
import org.megastage.components.dcpu.VirtualGyroscope;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

public class AttitudeControlSystem extends EntityProcessingSystem {
    ComponentMapper<VirtualGyroscope> GYROSCOPE;
    ComponentMapper<Rotation> ROTATION;
    ComponentMapper<ShipGeometry> SHIP_GEOMETRY;

    public AttitudeControlSystem() {
        super(Aspect.getAspectForAll(VirtualGyroscope.class));
    }

    @Override
    public void initialize() {
        GYROSCOPE = world.getMapper(VirtualGyroscope.class);
        ROTATION = world.getMapper(Rotation.class);
        SHIP_GEOMETRY = world.getMapper(ShipGeometry.class);
    }

    @Override
    protected void process(Entity entity) {
        VirtualGyroscope gyro = GYROSCOPE.get(entity);
        if(gyro.power == 0) return;

        ShipGeometry geom = SHIP_GEOMETRY.get(gyro.ship);
        if(geom == null) return;
        
        double angle = gyro.getRotation(geom) * world.getDelta();
        
        Rotation rotation = ROTATION.get(gyro.ship);
        Quaternion shipRotation = rotation.getQuaternion();

        Vector3d axis = gyro.axis.multiply(shipRotation);

        Quaternion gyroRotation = new Quaternion(axis, angle);
        shipRotation = gyroRotation.multiply(shipRotation).normalize();
        
        rotation.set(shipRotation);
    }
}
