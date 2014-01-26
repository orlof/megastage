package org.megastage.systems;

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
import org.megastage.components.srv.Identifier;
import org.megastage.components.srv.SynchronizeFlag;
import org.megastage.components.srv.UninitializedFlag;
import org.megastage.util.ServerGlobals;

public class ServerSynchronizeSystem extends EntitySystem {
    @Mapper ComponentMapper<UninitializedFlag> UNINITIALIZED_FLAG;

    private long interval;
    private long acc;
    
    public ServerSynchronizeSystem(long interval) {
        super(Aspect.getAspectForOne(SynchronizeFlag.class, UninitializedFlag.class));
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        if(ServerGlobals.time >= acc) {
                acc = ServerGlobals.time + interval;
                return true;
        }
        return false;
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        Bag bag = new Bag(100);
        
        Bag<Component> components = new Bag<>(20);
        for(int i=0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            boolean isUninitialized = UNINITIALIZED_FLAG.has(entity);
            
            components.clear();
            entity.getComponents(components);

            for(int j=0; j < components.size(); j++) {
                BaseComponent baseComponent = (BaseComponent) components.get(j);
                if(baseComponent.synchronize() || (isUninitialized && baseComponent.replicate())) {
                    if(isUninitialized) {
                        Identifier id = entity.getComponent(Identifier.class);
                        Log.info("replicating: " + id.toString() + " w " + baseComponent.toString());
                    }
                    
                    Object transferable = baseComponent.create(entity);
                    bag.add(transferable);
                }
            }
            
            if(isUninitialized) {
                entity.removeComponent(UninitializedFlag.class);
            }
        }
        
        Log.trace("Client state packet size updated: " + bag.size());
        ServerGlobals.updates = bag;
    }	
    
}
