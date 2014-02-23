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

    public static Array<Ship> findCollision(Array<Ship> candidates, Vector3d ray) {
        Array<Ship> collisions = new Array<>(candidates.size);

        for(Ship ship: candidates) {
            if(ray.x * ship.coord.x <= 0 && ray.y * ship.coord.y <= 0 && ray.z * ship.coord.z <= 0) {
                //behind the weapon
                continue;
            }
            
            ship.collisionRadius = Mapper.SHIP_GEOMETRY.get(ship.entity).map.getCollisionRadius();
            
            final double distanceToPoint = ray.distanceToPoint(ship.coord);
            
            if(distanceToPoint < ship.collisionRadius) {
                collisions.add(ship);
            }
        }
        
        return collisions;
    }
    
    public static Array<Ship> getShipsInRange(Entity me, Vector3d origo, double range) {
        double rangeSquared = range * range;
        
        Array<Ship> ships = new Array<>(entities.size);
        for(Entity e: entities) {
            if(me == e) continue;

            Vector3d coord = Mapper.POSITION.get(e).getVector3d().sub(origo);
            
            double distanceSquared = coord.lengthSquared();
            
            if(distanceSquared < rangeSquared) {
                ships.add(new Ship(e, coord, distanceSquared));
            }
        }
        
        ships.sort(new Comparator<Ship>() {
            // distance
            @Override
            public int compare(Ship o1, Ship o2) {
                return Double.compare(o1.distanceSquared, o2.distanceSquared);
            }
        });
        
        return ships;
    }

    public static class Ship {
        public Entity entity;
        public Vector3d coord;
        public double distanceSquared;
        public double collisionRadius;

        public Ship() {
        }

        private Ship(Entity entity, Vector3d coord, double distanceSquared) {
            this.entity = entity;
            this.coord = coord;
            this.distanceSquared = distanceSquared;
        }

    }
}
