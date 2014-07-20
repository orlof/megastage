package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.components.DeleteFlag;
import org.megastage.components.Explosion;
import org.megastage.ecs.CompType;

public class ExplosionSystem extends Processor {
    public ExplosionSystem(World world, long interval) {
        super(world, interval, CompType.Explosion);
    }

    @Override
    protected void process(int eid) {
        Explosion explosion = (Explosion) world.getComponent(eid, CompType.Explosion);
        
        explosion.setState(currentState(explosion));

        if(explosion.isDirty()) {
            switch(explosion.state) {
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
                    world.setComponent(eid, CompType.DeleteFlag, new DeleteFlag());
                    break;
            }
        }
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
        0, 1500, 3500, 3700, 4000, 13000, 13000, 15000
    };

    public int currentState(Explosion exp) {
        for(int state = -1; state+1 < delay.length; state++) {
            if(world.time < exp.startTime + delay[state+1]) {
                return state;
            }
        }
        return delay.length-1;
    }
}
