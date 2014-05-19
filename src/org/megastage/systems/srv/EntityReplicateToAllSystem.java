package org.megastage.systems.srv;

import com.esotericsoftware.minlog.Log;
import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.protocol.Message;
import org.megastage.util.ID;

public class EntityReplicateToAllSystem extends Processor {
    public EntityReplicateToAllSystem(World world, long interval) {
        super(world, interval, CompType.ReplicateToAllConnectionsFlag);
    }

    @Override
    protected void process(int eid) {
        Log.info(ID.get(eid));
        world.removeComponent(eid, CompType.ReplicateToAllConnectionsFlag);

        for(BaseComponent comp=world.compIter(eid, BaseComponent.class); comp != null; comp=world.compNext()) {
            Message msg = comp.replicate(eid);
            if(msg != null) {
                NetworkSystem.updates.add(msg);
            }
        }
    }
}
