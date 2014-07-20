package org.megastage.systems.client;

import org.megastage.util.Log;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.util.ID;

public class EntityDeleteSystem extends Processor {
    public EntityDeleteSystem(World world, long interval) {
        super(world, interval, CompType.DeleteFlag);
    }

    @Override
    protected void process(int eid) {
        Log.info(ID.get(eid));
        world.deleteEntity(eid);
    }
}
