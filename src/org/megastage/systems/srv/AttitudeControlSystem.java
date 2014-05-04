package org.megastage.systems.srv;

import org.megastage.components.Rotation;
import org.megastage.components.dcpu.VirtualGyroscope;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

public class AttitudeControlSystem extends Processor {
    public AttitudeControlSystem(World world, long interval) {
        super(world, interval, CompType.VirtualGyroscope);
    }

    @Override
    protected void process(int eid) {
        VirtualGyroscope gyro = (VirtualGyroscope) world.getComponent(eid, CompType.VirtualGyroscope);
        if(gyro.power == 0) return;

        ShipGeometry geom = (ShipGeometry) world.getComponent(gyro.shipEID, CompType.ShipGeometry);
        if(geom == null) return;
        
        double angle = gyro.getRotation(geom) * world.delta;
        
        Rotation rotation = (Rotation) world.getComponent(gyro.shipEID, CompType.Rotation);
        Quaternion shipRotation = rotation.getQuaternion4d();

        Vector3d axis = gyro.axis.multiply(shipRotation);

        Quaternion gyroRotation = new Quaternion(axis, angle);
        shipRotation = gyroRotation.multiply(shipRotation).normalize();
        
        rotation.set(shipRotation);
    }
}
