package org.megastage.systems.client;

import com.jme3.math.Quaternion;
import org.megastage.components.FixedRotation;
import org.megastage.components.Rotation;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.util.GlobalTime;

public class ClientFixedRotationSystem extends Processor {

    public ClientFixedRotationSystem(World world, long interval) {
        super(world, interval, CompType.Rotation, CompType.FixedRotation);
    }

    protected void process(int eid) {
        FixedRotation fr = (FixedRotation) world.getComponent(eid, CompType.FixedRotation);
        
        Quaternion rotation = new Quaternion().fromAngles(
                (float) fr.getX(GlobalTime.value), 
                (float) fr.getY(GlobalTime.value), 
                (float) fr.getZ(GlobalTime.value)).normalizeLocal();

        Rotation r = (Rotation) world.getComponent(eid, CompType.Rotation);
        r.x = rotation.getX();
        r.y = rotation.getY();
        r.z = rotation.getZ();
        r.w = rotation.getW();
    }
}
