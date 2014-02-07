package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.dcpu.VirtualEngine;
import org.megastage.util.Time;

public class ServerEngineTestSystem extends EntitySystem {
    private long interval;
    private long acc;
    
    private int state = 0;
    
    public ServerEngineTestSystem(long interval) {
        super(Aspect.getAspectForAll(VirtualEngine.class));
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
            VirtualEngine engine = entity.getComponent(VirtualEngine.class);
            switch(state++) {
                case 0:
                    engine.setPower((char) 0);
                    break;
                case 1:
                    engine.setPower((char) 0x8000);
                    break;
                case 2:
                    engine.setPower((char) 0xffff);
                    state = 0;
                    break;
            }
        }
    }	
}
