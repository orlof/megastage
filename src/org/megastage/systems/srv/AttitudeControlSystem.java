package org.megastage.systems.srv;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.megastage.components.Rotation;
import org.megastage.components.dcpu.VirtualGyroscope;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.EntitySystem;
import org.megastage.ecs.World;

public class AttitudeControlSystem extends EntitySystem {
    public AttitudeControlSystem(World world, long interval) {
        super(world, interval, CompType.VirtualGyroscope);
    }

    @Override
    protected void processEntity(int eid) throws ECSException {
        VirtualGyroscope gyro = (VirtualGyroscope) world.getComponentOrError(eid, CompType.VirtualGyroscope);
        if(gyro.value == 0) return;

        ShipGeometry geom = (ShipGeometry) world.getComponentOrError(gyro.shipEID, CompType.ShipGeometry);
        float angle = gyro.getAngularSpeed(geom) * world.delta;
        
        Rotation rotation = (Rotation) world.getComponent(gyro.shipEID, CompType.Rotation);

        Vector3f axis = rotation.rotate(gyro.axis.getVector());

        Quaternion gyroRotation = new Quaternion().fromAngleAxis(angle, axis);
        rotation.add(gyroRotation);
    }
}
