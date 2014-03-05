package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.CollisionSphere;
import org.megastage.components.Position;
import org.megastage.server.TargetManager;

/**
 * User: Orlof
 * Date: 8/19/13
 * Time: 12:09 PM
 */
public class TargetManagerSystem extends EntitySystem {
    public TargetManagerSystem() {
        super(Aspect.getAspectForAll(Position.class, CollisionSphere.class));
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        TargetManager.update(entities);
    }

}
