package org.megastage.systems.srv;

import com.esotericsoftware.minlog.Log;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.util.ID;

public class EntityInitializeSystem extends Processor {
    public EntityInitializeSystem(World world, long interval) {
        super(world, interval,  CompType.InitializeFlag);
    }

    @Override
    protected void process(int eid) {
        Log.info(ID.get(eid));
        
        world.removeComponent(eid, CompType.InitializeFlag);
        initializeComponents(eid);
    }

    private void initializeComponents(int eid) {
        for(Object c = world.compIter(eid); c != null; c=world.compNext()) {
            ((BaseComponent) c).initialize(world, eid);
        }
    }
}
