package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.dcpu.VirtualRadar;
import org.megastage.util.Mapper;
import org.megastage.util.GlobalTime;

public class ServerRadarTestSystem extends EntitySystem {
    private long interval;
    private long acc;
    
    public ServerRadarTestSystem(long interval) {
        super(Aspect.getAspectForAll(VirtualRadar.class));
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        if(GlobalTime.value >= acc) {
                acc = GlobalTime.value + interval;
                return true;
        }
        return false;
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
    }	
}
