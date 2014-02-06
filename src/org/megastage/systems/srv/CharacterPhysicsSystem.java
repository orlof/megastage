package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.Mode;
import org.megastage.components.Position;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.util.Time;

public class CharacterPhysicsSystem extends EntityProcessingSystem {
    ComponentMapper<Position> POSITION;
    ComponentMapper<BindTo> BIND_TO;
    ComponentMapper<ShipGeometry> SHIP_GEOMETRY;
    
    private long interval;
    private long acc;
    
    public CharacterPhysicsSystem(long interval) {
        super(Aspect.getAspectForAll(Mode.class));
        this.interval = interval;
    }

    @Override
    public void initialize() {
        POSITION = world.getMapper(Position.class);
        BIND_TO = world.getMapper(BindTo.class);
        SHIP_GEOMETRY = world.getMapper(ShipGeometry.class);
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
