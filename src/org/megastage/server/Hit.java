package org.megastage.server;

import org.megastage.ecs.World;
import org.megastage.util.CubeCollisionDetector;
import org.megastage.util.Vector3d;

public class Hit {
    public final double distance;

    public static Hit create(World world, Target target, Vector3d attackVector, float range) {
        if(target.isShip(world)) {
            return CubeCollisionDetector.hit(world, target, attackVector, range);
        } else if(target.isForceField(world)) {
            return new ForceFieldHit(target, attackVector);
        }
        
        return new NoHit();
    }

    public Hit() {
        this.distance = Double.MAX_VALUE;
    }

    public Hit(double distance) {
        this.distance = distance;
    }

    public String toString() {
        return "Hit(distance=" + distance;
    }
}

