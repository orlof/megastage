package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.protocol.Message;

public class EntitySynchronizeSystem extends Processor {
    public EntitySynchronizeSystem(World world, long interval) {
        super(world, interval, CompType.SynchronizeFlag);
    }

    @Override
    protected void process(int eid) {
        for(BaseComponent comp=world.compIter(eid, BaseComponent.class); comp != null; comp=world.compNext()) {
            Message msg = comp.synchronize(eid);
            if(msg != null) {
                NetworkSystem.updates.add(msg);
            }
        }
    }
}
