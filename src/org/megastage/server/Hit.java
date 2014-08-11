package org.megastage.server;

import org.megastage.ecs.ECSException;
import org.megastage.util.Ship;

public class Hit {
    public final float distance;
    public int attacker;

    public static Hit create(Target target) throws ECSException {
        if(target.isShip()) {
            return Ship.getHit(target);
        } else if(target.isForceField()) {
            return new ForceFieldHit(target);
        }
        
        return NoHit.INSTANCE;
    }

    public Hit(float distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Hit(distance=" + distance +", " + attacker + ")";
    }
}

