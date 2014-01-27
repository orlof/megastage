package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.Mode;
import org.megastage.components.Position;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.util.Time;

public class CharacterPhysicsSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Position> POSITION;
    @Mapper ComponentMapper<BindTo> BIND_TO;
    @Mapper ComponentMapper<ShipGeometry> SHIP_GEOMETRY;
    
    private long interval;
    private long acc;
    
    public CharacterPhysicsSystem(long interval) {
        super(Aspect.getAspectForAll(Mode.class));
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        if(Time.value >= acc) {
                acc = Time.value + interval;
                return true;
        }
        return false;
    }

    @Override
    protected void process(Entity e) {
    }
}
