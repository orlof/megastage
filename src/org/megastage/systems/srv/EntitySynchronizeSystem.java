package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.BaseComponent;
import org.megastage.components.srv.SynchronizeFlag;
import org.megastage.protocol.Message;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Time;

public class EntitySynchronizeSystem extends EntityProcessingSystem {
    private long interval;
    private long acc;
    
    public EntitySynchronizeSystem(long interval) {
        super(Aspect.getAspectForAll(SynchronizeFlag.class));
        this.interval = interval;
    }

    @Override
    public boolean checkProcessing() {
        if(Time.value >= acc) {
                acc = Time.value + interval;
                return true;
        }
        return false;
    }

    @Override
    protected void process(Entity entity) {
        synchronizeComponents(entity);
    }

    private Array<Component> _components = new Array<>(20);

    private void synchronizeComponents(Entity entity) {
        _components.clear();
        entity.getComponents(_components);

        for(Component c: _components) {
            Message msg = ((BaseComponent) c).synchronize(entity);
            if(msg != null) {
                ServerGlobals.updates.add(msg);
            }
        }
    }
}
