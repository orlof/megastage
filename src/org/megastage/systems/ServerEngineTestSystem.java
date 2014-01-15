package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.dcpu.Engine;
import org.megastage.util.ServerGlobals;

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
        if(ServerGlobals.time >= acc) {
                acc = ServerGlobals.time + interval;
                return true;
        }
        return false;
    }

    @Override
    protected void processEntities(ImmutableBag<Entity> entities) {
        for(int i=0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
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
