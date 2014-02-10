package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.dcpu.VirtualGyroscope;
import org.megastage.util.Time;

public class ServerGyroTestSystem extends EntitySystem {
    private long interval;
    private long acc;
    
    private int state = 0;
    
    public ServerGyroTestSystem(long interval) {
        super(Aspect.getAspectForAll(VirtualGyroscope.class));
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
            VirtualGyroscope gyro = entity.getComponent(VirtualGyroscope.class);
            switch(state) {
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
        state++;
    }	
}
