package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.components.Position;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.server.ShipManager;

/**
 * User: Orlof
 * Date: 8/19/13
 * Time: 12:09 PM
 */
public class ShipManagerSystem extends EntitySystem {
    public ShipManagerSystem() {
        super(Aspect.getAspectForAll(Position.class, ShipGeometry.class));
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        ShipManager.update(entities);
    }

}
