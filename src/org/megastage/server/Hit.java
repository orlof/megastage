package org.megastage.server;

import org.megastage.ecs.ECSException;
import org.megastage.util.Ship;

public class Hit {
    public final float distance;

    public static Hit create(Target target) throws ECSException {
        if(target.isShip()) {
            return Ship.getHit(target);
        } else if(target.isForceField()) {
            return new ForceFieldHit(target);
        }
        
        return NoHit.INSTANCE;
    }

    public Hit() {
        this.distance = Float.MAX_VALUE;
    }

    public Hit(float distance) {
        this.distance = distance;
    }

    public String toString() {
        return "Hit(distance=" + distance;
    }
}

