package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.Position;
import org.megastage.components.PrevPosition;
import org.megastage.util.Time;

public class PrevPositionSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Position> POSITION;
    @Mapper ComponentMapper<PrevPosition> PREV_POSITION;
    
    public PrevPositionSystem() {
        super(Aspect.getAspectForAll(PrevPosition.class, Position.class));
    }

    @Override
    protected void process(Entity e) {
        Position pos = POSITION.get(e);
        PrevPosition prev = PREV_POSITION.get(e);
        
        prev.time = Time.value;
        prev.x = pos.x;
        prev.y = pos.y;
        prev.z = pos.z;
    }
}
