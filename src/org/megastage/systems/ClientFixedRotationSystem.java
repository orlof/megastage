package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.FixedRotation;
import org.megastage.components.Rotation;

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

    @Override
    protected void process(Entity entity) {
        FixedRotation fr = FIXED_ROTATION.get(entity);
        
        Rotation r = ROTATION.get(entity);
        r.x = (float) fr.getX();
        r.y = (float) fr.getY();
        r.z = (float) fr.getZ();
    }

}
