package org.megastage.systems.srv;


import org.megastage.components.BindTo;
import org.megastage.components.generic.Flag;
import org.megastage.ecs.CompType;
import org.megastage.ecs.EntitySystem;
import org.megastage.ecs.World;

public class CleanupSystem extends EntitySystem {
    public CleanupSystem(World world, long interval) {
        super(world, interval, CompType.BindTo);
    }

    @Override
    protected void processEntity(int eid) {
        BindTo bindTo = (BindTo) world.getComponent(eid, CompType.BindTo);
        if(!world.hasEntity(bindTo.ref)) {
            world.setComponent(eid, CompType.FlagDelete, new Flag());
        }
    }
}
