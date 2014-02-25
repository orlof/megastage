package org.megastage.server;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import java.util.Comparator;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.util.ID;
import org.megastage.util.Mapper;
import org.megastage.util.Vector3d;

public class ShipManager {
    public static Array<Entity> entities = new Array<>(0);

    public static void update(Array<Entity> entities) {
        ShipManager.entities = entities;
    }

    public static Array<Target> findCollision(Array<Target> candidates, Vector3d ray) {
        Array<Target> collisions = new Array<>(candidates.size);

        for(Target target: candidates) {
            if(ray.x * target.coord.x <= 0 && ray.y * target.coord.y <= 0 && ray.z * target.coord.z <= 0) {
                //behind the weapon
                continue;
            }
            
            final double distanceToPoint = ray.distanceToPoint(target.coord);
            
            if(distanceToPoint < target.collisionRadius) {
                collisions.add(target);
            }
        }
        
        return collisions;
    }

//    static int count = 0;
    
    public static Array<Target> getTargetsInRange(Entity attShip, Vector3d wpnCoord, double wpnRange) {
        Array<Target> targets = new Array<>(entities.size);
        for(Entity target: entities) {
            if(attShip == target) continue;

            //Log.info(ID.get(e) + Mapper.POSITION.get(e).getGlobalVector3d(e));
            Vector3d coord = Mapper.POSITION.get(target).getLocalVector3d(target).sub(wpnCoord);
            // Log.info(ID.get(attShip) + ID.get(target) + wpnCoord.toString() + " to " + coord.toString());
            double colrad = Mapper.COLLISION_SPHERE.get(target).radius;
            double distance = coord.length() - colrad;

            // Log.info(distance + " +" + colrad);
            
            if(distance < wpnRange) {
                targets.add(new Target(target, coord, distance, colrad));
            }
        }
//        count++;
        
        targets.sort(new Comparator<Target>() {
            // distance
            @Override
            public int compare(Target o1, Target o2) {
                return Double.compare(o1.distance, o2.distance);
            }
        });
        
        return targets;
    }

    public static class Target {
        public Entity entity;
        public Vector3d coord;
        public double distance;
        public double collisionRadius;

        public Target() {
        }

        public Target(Entity entity, Vector3d coord, double distance, double colrad) {
            this.entity = entity;
            this.coord = coord;
            this.distance = distance;
            this.collisionRadius = colrad;
        }

    }
}
