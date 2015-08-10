package org.megastage.systems.srv;

import org.megastage.ecs.EntitySystem;
import org.megastage.util.Log;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.ID;

public class EntityInitializeSystem extends EntitySystem {
    public EntityInitializeSystem(World world, long interval) {
        super(world, interval,  CompType.FlagInitialize);
    }

    @Override
    protected void processEntity(int eid) {
        Log.debug(ID.get(eid));
        
        world.removeComponent(eid, CompType.FlagInitialize);
        initializeComponents(eid);
    }

    private void initializeComponents(int eid) {
        for(BaseComponent c = world.compIter(eid); c != null; c = world.compNext()) {
            c.initialize(eid);
        }
    }
}
