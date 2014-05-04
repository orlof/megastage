package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.protocol.Message;
import org.megastage.util.ServerGlobals;

public class EntitySynchronizeSystem extends Processor {
    public EntitySynchronizeSystem(World world, long interval) {
        super(world, interval, CompType.SynchronizeFlag);
    }

    @Override
    protected void process(int eid) {
        for(BaseComponent comp=world.components(eid, BaseComponent.class); comp != null; comp=world.nextComponent()) {
            Message msg = comp.synchronize(eid);
            if(msg != null) {
                ServerGlobals.updates.add(msg);
            }
        }
    }
}
