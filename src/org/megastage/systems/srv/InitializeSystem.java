package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.Bag;
import org.megastage.components.BaseComponent;
import org.megastage.components.srv.InitializeFlag;

public class InitializeSystem extends EntityProcessingSystem {
    
    public InitializeSystem() {
        super(Aspect.getAspectForAll(InitializeFlag.class));
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }

    @Override
    protected void process(Entity e) {
        Bag bag = e.getComponents(null);

        for(int i=0; i < bag.size(); i++) {
            BaseComponent c = (BaseComponent) bag.get(i);
            c.initialize(world, e);
        }

        e.removeComponent(InitializeFlag.class);
        e.changedInWorld();        
    }

    @Override
    protected void end() {
        world.deleteSystem(this);
    }
}
