package org.megastage.systems.srv;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.megastage.components.Rotation;
import org.megastage.components.dcpu.VirtualGyroscope;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;

public class AttitudeControlSystem extends Processor {
    public AttitudeControlSystem(World world, long interval) {
        super(world, interval, CompType.VirtualGyroscope);
    }

    @Override
    protected void process(int eid) {
        VirtualGyroscope gyro = (VirtualGyroscope) world.getComponent(eid, CompType.VirtualGyroscope);
        if(gyro.value == 0) return;

        ShipGeometry geom = (ShipGeometry) world.getComponent(gyro.shipEID, CompType.ShipGeometry);
        if(geom == null) return;
        
        float angle = gyro.getAngularSpeed(geom) * world.delta;
        
        Rotation rotation = (Rotation) world.getComponent(gyro.shipEID, CompType.Rotation);

        Vector3f axis = rotation.rotate(gyro.axis);

        Quaternion gyroRotation = new Quaternion().fromAngleAxis(angle, axis);
        rotation.add(gyroRotation);
    }
}
