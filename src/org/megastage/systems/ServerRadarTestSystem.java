package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.dcpu.VirtualRadar;
import org.megastage.components.dcpu.VirtualRadar.LocalRadarEcho;
import org.megastage.systems.srv.RadarEchoSystem.RadarData;
import org.megastage.util.ID;
import org.megastage.util.Time;

public class ServerRadarTestSystem extends EntitySystem {
    private long interval;
    private long acc;
    
    public ServerRadarTestSystem(long interval) {
        super(Aspect.getAspectForAll(VirtualRadar.class));
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
            RadarData candidate = null;
            VirtualRadar radar = entity.getComponent(VirtualRadar.class);
            
            for(LocalRadarEcho lre: radar.getSignatures()) {
                if(lre.distanceSquared > 0) {
                    candidate = lre.echo;
                }
            }
            
            if(candidate != null) {
                radar.setTrackingTarget(candidate);
                char[] mem = new char[7];
                radar.storeTargetDataToArray(candidate, mem, (char) 0);
            }
        }
    }	
}
