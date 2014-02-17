package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.srv.GravityFieldFlag;
import org.megastage.components.Mass;
import org.megastage.components.Position;
import org.megastage.server.GravityManager;

public class GravityManagerSystem extends EntitySystem {
    public GravityManagerSystem() {
        super(Aspect.getAspectForAll(GravityFieldFlag.class, Position.class, Mass.class));
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        GravityManager.update(entities);
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }
}
