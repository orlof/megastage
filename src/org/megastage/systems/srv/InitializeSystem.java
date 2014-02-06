package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.BaseComponent;
import org.megastage.components.srv.InitializeFlag;

public class InitializeSystem extends EntityProcessingSystem {
    
    public InitializeSystem() {
        super(Aspect.getAspectForAll(InitializeFlag.class));
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    @Override
    protected void process(Entity e) {
        Array<Component> bag = new Array<>(20);
        e.getComponents(bag);

        for(Component c: (Array<Component>) bag) {
            BaseComponent bc = (BaseComponent) c;
            bc.initialize(world, e);
        }

        e.removeComponent(InitializeFlag.class);
        e.changedInWorld();        
    }

    @Override
    protected void end() {
        world.deleteSystem(this);
    }
}
