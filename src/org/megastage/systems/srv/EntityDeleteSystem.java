package org.megastage.systems.srv;

import com.esotericsoftware.minlog.Log;
import org.megastage.components.DeleteFlag;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.protocol.Network.ComponentMessage;
import org.megastage.util.ID;

public class EntityDeleteSystem extends Processor {
    public EntityDeleteSystem(World world, long interval) {
        super(world, interval, CompType.DeleteFlag);
    }

    @Override
    protected void process(int eid) {
        Log.info(ID.get(eid));
        DeleteFlag df = (DeleteFlag) world.getComponent(eid, CompType.DeleteFlag);
        NetworkSystem.updates.add(new ComponentMessage(eid, df));
        world.deleteEntity(eid);
    }
}
