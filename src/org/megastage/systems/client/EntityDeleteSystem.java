package org.megastage.systems.client;

import org.megastage.ecs.EntitySystem;
import org.megastage.util.Log;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.ID;

public class EntityDeleteSystem extends EntitySystem {
    public EntityDeleteSystem(World world, long interval) {
        super(world, interval, CompType.DeleteFlag);
    }

    @Override
    protected void processEntity(int eid) {
        Log.debug(ID.get(eid));
        world.deleteEntity(eid);
    }
}
