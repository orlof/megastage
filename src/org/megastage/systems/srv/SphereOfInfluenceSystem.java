package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.Position;
import org.megastage.components.srv.SphereOfInfluence;
import org.megastage.server.SOIManager;
import org.megastage.util.Time;

/**
 * User: Orlof
 */
public class SphereOfInfluenceSystem extends EntitySystem {
    private long interval;
    private long acc;

    public SphereOfInfluenceSystem(long interval) {
        super(Aspect.getAspectForAll(SphereOfInfluence.class, Position.class));
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
        SOIManager.update(entities);
    }
}
