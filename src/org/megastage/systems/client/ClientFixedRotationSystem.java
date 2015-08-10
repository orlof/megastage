package org.megastage.systems.client;

import com.jme3.math.Quaternion;
import org.megastage.components.FixedRotation;
import org.megastage.components.Rotation;
import org.megastage.ecs.CompType;
import org.megastage.ecs.EntitySystem;
import org.megastage.ecs.World;

public class ClientFixedRotationSystem extends EntitySystem {

    public ClientFixedRotationSystem(World world, long interval) {
        super(world, interval, CompType.Rotation, CompType.FixedRotation);
    }

    @Override
    protected void processEntity(int eid) {
        FixedRotation fr = (FixedRotation) world.getComponent(eid, CompType.FixedRotation);
        
        Quaternion rotation = new Quaternion().fromAngles(
                (float) fr.getX(world.time), 
                (float) fr.getY(world.time), 
                (float) fr.getZ(world.time)).normalizeLocal();

        Rotation r = (Rotation) world.getComponent(eid, CompType.Rotation);
        r.set(rotation);
    }
}
