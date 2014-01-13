package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.dcpu.Engine;
import org.megastage.components.dcpu.Gyroscope;
import org.megastage.util.ServerGlobals;

public class ServerGyroTestSystem extends EntitySystem {
    private long interval;
    private long acc;
    
    private int state = 0;
    
    public ServerGyroTestSystem(long interval) {
        super(Aspect.getAspectForAll(Gyroscope.class));
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
            Gyroscope gyro = entity.getComponent(Gyroscope.class);
            switch(state++) {
                case 0:
                    gyro.setTorque((char) 0);
                    break;
                case 1:
                    gyro.setTorque((char) 0x7fff);
                    break;
                case 2:
                    gyro.setTorque((char) 0x8000);
                    state = 0;
                    break;
            }
        }
    }	
}
