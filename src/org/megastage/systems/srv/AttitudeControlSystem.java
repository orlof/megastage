package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Rotation;
import org.megastage.components.dcpu.Gyroscope;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class AttitudeControlSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Gyroscope> GYROSCOPE;
    @Mapper ComponentMapper<Rotation> ROTATION;
    @Mapper ComponentMapper<ShipGeometry> SHIP_GEOMETRY;

    public AttitudeControlSystem() {
        super(Aspect.getAspectForAll(Gyroscope.class));
    }

    @Override
    protected void process(Entity entity) {
        Gyroscope gyro = GYROSCOPE.get(entity);
        if(gyro.power == 0) return;

        ShipGeometry geom = SHIP_GEOMETRY.get(gyro.ship);
        if(geom == null) return;
        
        double angle = gyro.getRotation(geom) * world.delta;
        
        Rotation rotation = ROTATION.get(gyro.ship);
        Quaternion shipRotation = rotation.getQuaternion();

        Vector axis = gyro.axis.multiply(shipRotation);

        Quaternion gyroRotation = new Quaternion(axis, angle);
        shipRotation = gyroRotation.multiply(shipRotation).normalize();
        
        rotation.set(shipRotation);
    }
}
