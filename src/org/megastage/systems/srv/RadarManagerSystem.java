package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.Position;
import org.megastage.components.Mass;
import org.megastage.components.RadarEcho;
import org.megastage.server.RadarManager;
import org.megastage.util.Time;

/**
 * User: Orlof
 * Date: 8/19/13
 * Time: 12:09 PM
 */
public class RadarManagerSystem extends EntitySystem {
    private long interval;
    private long acc;

    public RadarManagerSystem(long interval) {
        super(Aspect.getAspectForAll(Mass.class, Position.class, RadarEcho.class));
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
        RadarManager.update(entities);
    }

}
