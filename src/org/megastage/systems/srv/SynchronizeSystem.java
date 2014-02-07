package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.BaseComponent;
import org.megastage.components.DeleteFlag;
import org.megastage.components.srv.Identifier;
import org.megastage.components.srv.SynchronizeFlag;
import org.megastage.components.srv.UninitializedFlag;
import org.megastage.protocol.Message;
import org.megastage.util.ID;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Time;

public class SynchronizeSystem extends EntitySystem {
    ComponentMapper<UninitializedFlag> UNINITIALIZED_FLAG;
    ComponentMapper<DeleteFlag> DELETE_FLAG;

    private long interval;
    private long acc;
    
    public SynchronizeSystem(long interval) {
        super(Aspect.getAspectForOne(SynchronizeFlag.class, UninitializedFlag.class, DeleteFlag.class));
        this.interval = interval;
    }

    @Override
    public void initialize() {
        
        UNINITIALIZED_FLAG = world.getMapper(UninitializedFlag.class);
        DELETE_FLAG = world.getMapper(DeleteFlag.class);
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
    protected void processEntities(Array<Entity> entities) {
        Log.trace("" + Time.value);

        for(Entity entity: entities) {
            if(DELETE_FLAG.has(entity)) {
                world.deleteEntity(entity);
                ServerGlobals.updates.add(new DeleteFlag().create(entity));
            } else if(UNINITIALIZED_FLAG.has(entity)) {
                entity.removeComponent(UninitializedFlag.class);
                replicateComponents(ServerGlobals.updates, entity);
            } else {
                synchronizeComponents(ServerGlobals.updates, entity);
            }
        }
        
        Log.trace("Number of components to synchronize: " + ServerGlobals.updates.size);
    }	

    private Array<Component> _components = new Array<>(20);

    private Array<Message> synchronizeComponents(Array<Message> fillBag, Entity entity) {
        _components.clear();
        entity.getComponents(_components);

        for(Component c: _components) {
            BaseComponent bc = (BaseComponent) c;
            if(bc.synchronize()) {
                fillBag.add(bc.create(entity));
            }
        }
        return fillBag;
    }

    private Array<Message> replicateComponents(Array<Message> fillBag, Entity entity) {
        Log.info(ID.get(entity));

        _components.clear();
        entity.getComponents(_components);

        for(int j=0; j < _components.size; j++) {
            BaseComponent baseComponent = (BaseComponent) _components.get(j);
            if(baseComponent.replicate()) {
                fillBag.add(baseComponent.create(entity));

                if(Log.INFO) {
                    Log.info(" " + baseComponent.toString());
                }
            }
        }

        return fillBag;
    }
}
