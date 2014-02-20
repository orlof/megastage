package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.srv.GravityFieldFlag;
import org.megastage.components.Mass;
import org.megastage.components.Position;
import org.megastage.server.GravityManager;
import org.megastage.util.Time;

public class GravityManagerSystem extends EntitySystem {
    private long interval;
    private long acc;

    public GravityManagerSystem(long interval) {
        super(Aspect.getAspectForAll(GravityFieldFlag.class, Position.class, Mass.class));
        this.interval = interval;
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        GravityManager.update(entities);
    }

    @Override
    protected boolean checkProcessing() {
        if(Time.value >= acc) {
                acc = Time.value + interval;
                return true;
        }
        return false;
    }

}
