package org.megastage.systems.srv;

import org.megastage.components.DeleteFlag;
import org.megastage.components.gfx.BindTo;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;

public class CleanupSystem extends Processor {
    public CleanupSystem(World world, long interval) {
        super(world, interval, CompType.BindTo);
    }

    @Override
    protected void process(int eid) {
        BindTo bindTo = (BindTo) world.getComponent(eid, CompType.BindTo);
        if(!world.hasEntity(bindTo.parent)) {
            world.setComponent(eid, CompType.DeleteFlag, new DeleteFlag());
        }
    }
    
}
