package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.DeleteFlag;
import org.megastage.components.gfx.BindTo;
import org.megastage.util.Mapper;
import org.megastage.util.Time;

public class CleanupSystem extends EntityProcessingSystem {
    private long interval;
    private long acc;
    
    public CleanupSystem(long interval) {
        super(Aspect.getAspectForAll(BindTo.class));
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
        BindTo bindTo = Mapper.BIND_TO.get(e);
        if(world.getEntity(bindTo.parent) == null) {
            e.addComponent(new DeleteFlag());
        }
    }
    
}
