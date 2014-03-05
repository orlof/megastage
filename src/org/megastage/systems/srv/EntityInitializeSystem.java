package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.BaseComponent;
import org.megastage.components.srv.InitializeFlag;
import org.megastage.components.srv.ReplicateFlag;
import org.megastage.protocol.Message;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Time;

public class EntityInitializeSystem extends EntityProcessingSystem {
    private long interval;
    private long acc;
    
    public EntityInitializeSystem(long interval) {
        super(Aspect.getAspectForAll(InitializeFlag.class));
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
        entity.removeComponent(InitializeFlag.class);
        entity.changedInWorld();

        initializeComponents(entity);
    }

    private Array<Component> _components = new Array<>(20);

    private void initializeComponents(Entity entity) {
        _components.clear();
        entity.getComponents(_components);

        for(Component comp: _components) {
            ((BaseComponent) comp).initialize(world, entity);
        }
    }
}
