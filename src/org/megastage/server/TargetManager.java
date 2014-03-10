package org.megastage.server;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.cubes.Vector3Int;
import com.esotericsoftware.minlog.Log;
import java.util.Comparator;
import org.megastage.components.Position;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.util.CubeCollisionDetector;
import org.megastage.util.ID;
import org.megastage.util.Mapper;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

public class TargetManager {
    public static Array<Entity> entities = new Array<>(0);

    public static void update(Array<Entity> entities) {
        TargetManager.entities = entities;
    }

    public static Hit findHit(Entity vtlEntity, VirtualThermalLaser vtlComponent) {
        Array<Target> targets = TargetManager.getTargetsInRange(vtlEntity, vtlComponent);

        if(targets == null || targets.size == 0) {
            return TargetManager.NO_HIT;
        }

        Quaternion shipAngle = Mapper.ROTATION.get(vtlComponent.ship).getQuaternion4d();
        Quaternion weaponAngle = Mapper.ROTATION.get(vtlEntity).getQuaternion4d();
        Vector3d attackVector = Vector3d.FORWARD.multiply(weaponAngle).multiply(shipAngle);
        
        //Log.info(attackVector.toString());
        //Vector3d attackVector = shipVector.multiply(weaponAngle);

        Array<Target> collisions = new Array<>(targets.size);

        Hit bestHit = NO_HIT;
        for(Target target: targets) {
            //Log.info("processing: " + target.toString());
            if(target.closestDistance > bestHit.distance) {
                //Log.info("no more closer targets " + target.toString());
                return bestHit;
            }

            if(target.vtlIsInCollisionSphere()) {
                // TODO special case
                //Log.info("inside collision sphere " + target.toString());
                continue;
            } else if(target.isBehind(attackVector)) {
                //Log.info("target behind " + target.toString());
                continue;
            }
            
            target.setDistanceFromLOF(attackVector);
            
            if(!target.isInLOF()) {
                //Log.info("not in line of fire " + target.toString());
                continue;
            }

            Hit hit = Hit.create(target, attackVector, vtlComponent.range);
            //Log.info(hit.toString());

            if(hit.distance < bestHit.distance) {
                //Log.info("new hit selected: " + bestHit.toString() + " becomes " + hit.toString() + " to " + target.toString());
                bestHit = hit;
            }
        }

        return bestHit;
    }

    public static Array<Target> getTargetsInRange(Entity vtlEntity, VirtualThermalLaser vtlComponent) {
        Position vtlPosition = Mapper.POSITION.get(vtlEntity);
        if(vtlPosition == null) {
            Log.error("VTL Position is null");
            return null;
        }

        Vector3d vtlCoord = vtlPosition.getGlobalCoordinates(vtlEntity);
        if(vtlCoord == null) {
            Log.error("Cannot convert VTL Position to global coordinates");
            return null;
        }

        Array<Target> targets = new Array<>(entities.size);

        for(Entity target: entities) {
            if(vtlComponent.ship == target) {
                // can't shoot itself ...for now
                continue;
            }
            
            Position targetPos = Mapper.POSITION.get(target);
            if(targetPos == null) {
                Log.error("No Position for target: " + ID.get(target));
                continue;
            }

            Vector3d targetCoord = targetPos.getGlobalCoordinates(target);
            if(targetCoord == null) {
                Log.error("Cannot convert Position to global coordinates: " + ID.get(target));
                continue;
            }

            // transfer target coordinates to vtl coordinates
            targetCoord = targetCoord.sub(vtlCoord);
            
            double colrad = Mapper.COLLISION_SPHERE.get(target).radius;
            double distanceSquared = targetCoord.lengthSquared();
            double closestDistance = Math.sqrt(distanceSquared) - colrad;

            //Log.info("XXXXXXXXXXXXXXXXXXXXXXX" + vtlCoord.toString() + " " + targetCoord.toString());
            
            if(closestDistance < vtlComponent.range) {
                targets.add(new Target(target, targetCoord, distanceSquared, closestDistance, colrad));
            }
        }
        
        targets.sort(new Comparator<Target>() {
            // distance
            @Override
            public int compare(Target o1, Target o2) {
                return Double.compare(o1.closestDistance, o2.closestDistance);
            }
        });
        
        return targets;
    }

    private static Hit getActualHit(Target target, Vector3d ray, double range) {
        return null;
    }

    public static class Target {
        public Entity entity;
        public Vector3d coord;
        public double closestDistance;
        public double collisionRadius;
        private double distanceFromLOF;
        private double distanceSquared;

        public Target() {
        }

        public Target(Entity entity, Vector3d coord, double distanceSquared, double closestDistance, double colrad) {
            this.entity = entity;
            this.coord = coord;
            this.distanceSquared = distanceSquared;
            this.closestDistance = closestDistance;
            this.collisionRadius = colrad;
        }

        private boolean vtlIsInCollisionSphere() {
            return this.closestDistance < 0.0;
        }

        private boolean isBehind(Vector3d ray) {
            return ray.x * coord.x <= 0 && ray.y * coord.y <= 0 && ray.z * coord.z <= 0;
        }

        private void setDistanceFromLOF(Vector3d attackVector) {
            this.distanceFromLOF = attackVector.distanceToPoint(coord);
        }

        private boolean isInLOF() {
            return distanceFromLOF <= collisionRadius;
        }

        private boolean isShip() {
            return Mapper.SHIP_GEOMETRY.has(entity);
        }

        private boolean isForceField() {
            return Mapper.VIRTUAL_FORCE_FIELD.has(entity);
        }

        private double getImpactDistance(Vector3d attackVector) {
            double distanceFromLOFSquared = distanceFromLOF * distanceFromLOF;
            double side = Math.sqrt(distanceSquared - distanceFromLOFSquared);
            side -= Math.sqrt(collisionRadius*collisionRadius - distanceFromLOFSquared);
            return side;
        }

        @Override
        public String toString() {
            return "Target{" + "entity=" + ID.get(entity) + ", coord=" + coord + ", closestDistance=" + closestDistance + ", collisionRadius=" + collisionRadius + ", distanceFromLOF=" + distanceFromLOF + ", distanceSquared=" + distanceSquared + '}';
        }
        
        
    }
    
    public static class Hit {
        public final double distance;

        private static Hit create(Target target, Vector3d attackVector, float range) {
            if(target.isShip()) {
                return CubeCollisionDetector.hit(target, attackVector, range);
            } else if(target.isForceField()) {
                return new ForceFieldHit(target, attackVector);
            }
            return NO_HIT;
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
    
    public static class NoHit extends Hit {
        public String toString() {
            return "NoHit(distance=" + distance;
        }
    }
    public static class ShipStructureHit extends Hit {
        public Entity entity;
        public final Vector3Int block;
        public final Vector3d coord;

        public ShipStructureHit(Target target, Vector3Int block, Vector3d coord, double distance) {
            super(distance);
            this.entity = target.entity;
            this.block = block;
            this.coord = coord;
        }

        public String toString() {
            return "ShipStructureHit(distance=" + distance + ", block=" + block.toString() + ", coord=" + coord.toString() +")";
        }
}
    public static class ForceFieldHit extends Hit {
        public Entity entity;

        private ForceFieldHit(Target target, Vector3d attackVector) {
            super(target.getImpactDistance(attackVector));
            this.entity = target.entity;
        }

        public String toString() {
            return "ForceFieldHit(distance=" + distance + ", entity=" + ID.get(entity) +")";
        }
    }
    
    public static final Hit NO_HIT = new NoHit();
}
