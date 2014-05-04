package org.megastage.systems.srv;

import org.megastage.components.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;

public class EntityInitializeSystem extends Processor {
    public EntityInitializeSystem(World world, long interval) {
        super(world, interval, CompType.InitializeFlag);
    }

    @Override
    protected void process(int eid) {
        world.removeComponent(eid, CompType.InitializeFlag);
        initializeComponents(eid);
    }

    private void initializeComponents(int eid) {
        for(Object c = world.components(eid); c != null; c=world.nextComponent()) {
            ((BaseComponent) c).initialize(world, eid);
        }
    }
}
