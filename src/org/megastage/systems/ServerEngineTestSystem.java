package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.dcpu.Engine;
import org.megastage.util.Time;

public class ServerEngineTestSystem extends EntitySystem {
    private long interval;
    private long acc;
    
    private int state = 0;
    
    public ServerEngineTestSystem(long interval) {
        super(Aspect.getAspectForAll(Engine.class));
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
    protected void processEntities(Array<Entity> entities) {
        for(Entity entity: entities) {
            Engine engine = entity.getComponent(Engine.class);
            switch(state++) {
                case 0:
                    engine.setPowerTarget((char) 0);
                    break;
                case 1:
                    engine.setPowerTarget((char) 0x8000);
                    break;
                case 2:
                    engine.setPowerTarget((char) 0xffff);
                    state = 0;
                    break;
            }
        }
    }	
}
