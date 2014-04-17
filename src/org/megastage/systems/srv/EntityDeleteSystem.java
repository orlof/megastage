package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.BaseComponent;
import org.megastage.components.DeleteFlag;
import org.megastage.components.srv.Identifier;
import org.megastage.components.srv.SynchronizeFlag;
import org.megastage.components.srv.ReplicateFlag;
import org.megastage.protocol.Message;
import org.megastage.protocol.Network.ComponentMessage;
import org.megastage.util.ID;
import org.megastage.util.Mapper;
import org.megastage.util.ServerGlobals;
import org.megastage.util.GlobalTime;

public class EntityDeleteSystem extends EntityProcessingSystem {
    private long interval;
    private long acc;
    
    public EntityDeleteSystem(long interval) {
        super(Aspect.getAspectForAll(DeleteFlag.class));
        this.interval = interval;
    }

    @Override
    public boolean checkProcessing() {
        return true;
//        if(Time.value >= acc) {
//                acc = Time.value + interval;
//                return true;
//        }
//        return false;
    }

    @Override
    protected void process(Entity entity) {
        ServerGlobals.updates.add(new ComponentMessage(entity, Mapper.DELETE_FLAG.get(entity)));
        world.deleteEntity(entity);
    }
}
