package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.BaseComponent;
import org.megastage.components.srv.ReplicateFlag;
import org.megastage.protocol.Message;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Time;

public class EntityReplicateSystem extends EntityProcessingSystem {
    private long interval;
    private long acc;
    
    public EntityReplicateSystem(long interval) {
        super(Aspect.getAspectForAll(ReplicateFlag.class));
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
        entity.removeComponent(ReplicateFlag.class);
        entity.changedInWorld();

        replicateComponents(entity);
    }

    private Array<Component> _components = new Array<>(20);

    private void replicateComponents(Entity entity) {
        _components.clear();
        entity.getComponents(_components);

        for(Component comp: _components) {
            Message msg = ((BaseComponent) comp).replicate(entity);
            if(msg != null) {
                ServerGlobals.updates.add(msg);
            }
        }
    }
}
