package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.jme3.math.Quaternion;
import org.megastage.components.FixedRotation;
import org.megastage.components.Rotation;
import org.megastage.client.ClientGlobals;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientFixedRotationSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Rotation> ROTATION;
    @Mapper ComponentMapper<FixedRotation> FIXED_ROTATION;

    public ClientFixedRotationSystem() {
        super(Aspect.getAspectForAll(Rotation.class, FixedRotation.class));
    }

    protected void process(Entity entity) {
        FixedRotation fr = FIXED_ROTATION.get(entity);
        
        Quaternion rotation = new Quaternion().fromAngles(
                (float) fr.getX(ClientGlobals.time), 
                (float) fr.getY(ClientGlobals.time), 
                (float) fr.getZ(ClientGlobals.time)).normalizeLocal();

        Rotation r = ROTATION.get(entity);
        r.x = rotation.getX();
        r.y = rotation.getY();
        r.z = rotation.getZ();
        r.w = rotation.getW();
    }
}
