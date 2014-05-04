package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.protocol.Message;
import org.megastage.util.ServerGlobals;

public class EntityReplicateSystem extends Processor {
    public EntityReplicateSystem(World world, long interval) {
        super(world, interval, CompType.ReplicateFlag);
    }

    @Override
    protected void process(int eid) {
        world.removeComponent(eid, CompType.ReplicateFlag);

        for(BaseComponent comp=world.components(eid, BaseComponent.class); comp != null; comp=world.nextComponent()) {
            Message msg = comp.replicate(eid);
            if(msg != null) {
                ServerGlobals.updates.add(msg);
            }
        }
    }
}
