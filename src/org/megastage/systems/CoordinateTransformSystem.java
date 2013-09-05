package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.LocalPosition;
import org.megastage.components.Position;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoordinateTransformSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Position> positionMapper;
    @Mapper ComponentMapper<LocalPosition> localPositionMapper;

    public CoordinateTransformSystem() {
        super(Aspect.getAspectForAll(Position.class, LocalPosition.class));
    }

    @Override
    protected void process(Entity entity) {
        LocalPosition locPos = localPositionMapper.get(entity);
        Position pos = positionMapper.get(entity);
        
        pos.x = locPos.x;
        pos.y = locPos.y;
        pos.z = locPos.z;
        
        while(localPositionMapper.has(locPos.parent)) {
            locPos = localPositionMapper.get(locPos.parent);
            pos.x += locPos.x;
            pos.y += locPos.y;
            pos.z += locPos.z;
        }

        Position center = positionMapper.get(locPos.parent);
        pos.x += center.x;
        pos.y += center.y;
        pos.z += center.z;
    }
}
