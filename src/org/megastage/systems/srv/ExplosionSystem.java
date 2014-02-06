package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.DeleteFlag;
import org.megastage.components.Explosion;
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

    ComponentMapper<Explosion> EXPLOSION;
    
    public ExplosionSystem(long interval) {
        super(Aspect.getAspectForAll(Explosion.class));
        this.interval = interval;
    }

    @Override
    public void initialize() {
        EXPLOSION = world.getMapper(Explosion.class);
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
    protected void process(Entity e) {
        Explosion explosion = EXPLOSION.get(e);

        if(isSynchronized(explosion) && stateExpired(explosion)) {
            switch(explosion.serverState) {
                case 0: // create spatial
                case 1: // particles
                case 2: // particles, ligth
                case 3: // particles
                case 4: // remove ship spatial
                case 5: // kill particles
                case 6: // kill particles, remove light, remove ship node
                    //ServerGlobals.addUDPEvent(new ExplosionEvent(e, explosion.state));
                    break;
                case 7:
                    e.addComponent(new DeleteFlag());
                    break;
            }
            explosion.serverState++;
            Log.trace("serverState advance " + explosion.serverState);
        }
    }

    public boolean isSynchronized(Explosion explosion) {
        return explosion.clientState == explosion.serverState;
    }

    // create spatial
    // 1500
    // sparks
    // 3500
    // burst, ligth
    // 3700
    // explosion
    // 8000
    // remove ship spatial
    // 8000
    // kill particles
    // kill particles, remove light, remove ship node
    
    private static final long[] delay = new long[] {
//        1500, 3500, 3700, 8000, 8000, 13000, 14000, 15000
        0, 1500, 3500, 3700, 8000, 13000, 13000, 13000
    };

    public boolean stateExpired(Explosion explosion) {
        long time = Time.value - explosion.startTime;
        return time > delay[explosion.serverState];
    }
}
