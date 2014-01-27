package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.BaseComponent;
import org.megastage.components.DeleteFlag;
import org.megastage.components.srv.Identifier;
import org.megastage.components.srv.SynchronizeFlag;
import org.megastage.components.srv.UninitializedFlag;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Time;

public class SynchronizeSystem extends EntitySystem {
    @Mapper ComponentMapper<UninitializedFlag> UNINITIALIZED_FLAG;
    @Mapper ComponentMapper<DeleteFlag> DELETE_FLAG;

    private long interval;
    private long acc;
    
    public SynchronizeSystem(long interval) {
        super(Aspect.getAspectForOne(SynchronizeFlag.class, UninitializedFlag.class, DeleteFlag.class));
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        if(Time.value >= acc) {
                acc = Time.value + interval;
                return true;
        }
        return false;
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        Log.trace("" + Time.value);

        Bag bag = new Bag(100);
        
        for(int i=0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            if(DELETE_FLAG.has(entity)) {
                world.deleteEntity(entity);
                bag.add(new DeleteFlag().create(entity));
            } else if(UNINITIALIZED_FLAG.has(entity)) {
                entity.removeComponent(UninitializedFlag.class);
                replicateComponents(bag, entity);
            } else {
                synchronizeComponents(bag, entity);
            }
        }
        
        Log.trace("Number of components to synchronize: " + bag.size());
        ServerGlobals.updates = bag;
    }	

    private Bag<Component> _components = new Bag<>(20);
    private Bag synchronizeComponents(Bag fillBag, Entity entity) {
        _components.clear();
        entity.getComponents(_components);

        for(int j=0; j < _components.size(); j++) {
            BaseComponent baseComponent = (BaseComponent) _components.get(j);
            if(baseComponent.synchronize()) {
                fillBag.add(baseComponent.create(entity));
            }
        }
        return fillBag;
    }

    private Bag replicateComponents(Bag fillBag, Entity entity) {
        Log.info(entity.getComponent(Identifier.class).toString());

        _components.clear();
        entity.getComponents(_components);

        for(int j=0; j < _components.size(); j++) {
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
