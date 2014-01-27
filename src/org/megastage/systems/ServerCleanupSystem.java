package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.DeleteFlag;
import org.megastage.components.gfx.BindTo;
import org.megastage.util.ServerGlobals;

public class ServerCleanupSystem extends EntityProcessingSystem {
    private long interval;
    private long acc;
    
    public ServerCleanupSystem(long interval) {
        super(Aspect.getAspectForAll(BindTo.class));
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        if(ServerGlobals.time >= acc) {
                acc = ServerGlobals.time + interval;
                return true;
        }
        return false;
    }

    @Override
    protected void process(Entity e) {
        BindTo bindTo = e.getComponent(BindTo.class);
        if(world.getEntity(bindTo.parent) == null) {
            e.addComponent(new DeleteFlag());
        }
    }
    
}
