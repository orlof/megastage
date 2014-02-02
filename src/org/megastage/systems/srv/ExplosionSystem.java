package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.DeleteFlag;
import org.megastage.components.Explosion;
import org.megastage.protocol.Network.ComponentMessage;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Time;

/**
 * Created with IntelliJ IDEA.
 * User: Orlof
 * Date: 8/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExplosionSystem extends EntityProcessingSystem {
    private long interval;
    private long acc;

    @Mapper ComponentMapper<Explosion> EXPLOSION;
    
    public ExplosionSystem(long interval) {
        super(Aspect.getAspectForAll(Explosion.class));
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

    private static final long[] delay = new long[] {
        0, 0, 1500, 3500, 3700, 8000, 12000, 13000
    };

    @Override
    protected void process(Entity e) {
        Explosion explosion = EXPLOSION.get(e);

        long time = Time.value - explosion.startTime;

        while(explosion.state < delay.length && time > delay[explosion.state]) {
            switch(explosion.state) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    //ServerGlobals.addUDPEvent(new ExplosionEvent(e, explosion.state));
                    Log.trace("explosion state change: " + explosion.state);
                    ServerGlobals.addComponentEvent(new ComponentMessage(e, explosion.copy()));
                    break;
                case 7:
                    e.addComponent(new DeleteFlag());
                    break;
            }
            explosion.state++;
        }
    }
}
