package org.megastage.systems.srv;

import com.esotericsoftware.minlog.Log;
import org.megastage.components.CollisionSphere;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.server.Hit;
import org.megastage.server.NoHit;
import org.megastage.server.Target;
import org.megastage.util.Bag;
import org.megastage.util.ID;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

public class TargetManagerSystem extends Processor {
    public static TargetManagerSystem INSTANCE;
    
    public TargetManagerSystem(World world, long interval) {
        super(world, interval, CompType.Position, CompType.CollisionSphere);
    }

    public Hit findHit(int vtlEntity, VirtualThermalLaser vtlComponent) {
        int ship = vtlComponent.shipEID;
        Bag<Target> targets = getTargetsInRange(ship, vtlEntity, vtlComponent);

        if(targets == null || targets.size() == 0) {
            return new NoHit();
        }

        Rotation shipRot = (Rotation) world.getComponent(ship, CompType.Rotation);
        Quaternion shipAngle = shipRot.getQuaternion4d();
        
        Rotation wpnRot = (Rotation) world.getComponent(vtlEntity, CompType.Rotation);
        Quaternion weaponAngle = wpnRot.getQuaternion4d();
        
        Vector3d attackVector = Vector3d.FORWARD.multiply(weaponAngle).multiply(shipAngle);
        
        //Log.info(attackVector.toString());
        //Vector3d attackVector = shipVector.multiply(weaponAngle);

        Bag<Target> collisions = new Bag<>(targets.size());

        Hit bestHit = new NoHit();
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

            Hit hit = Hit.create(world, target, attackVector, vtlComponent.range);
            //Log.info(hit.toString());

            if(hit.distance < bestHit.distance) {
                //Log.info("new hit selected: " + bestHit.toString() + " becomes " + hit.toString() + " to " + target.toString());
                bestHit = hit;
            }
        }

        return bestHit;
    }

    public Bag<Target> getTargetsInRange(int ship, int vtlEntity, VirtualThermalLaser vtlComponent) {
        Position vtlPosition = (Position) world.getComponent(vtlEntity, CompType.Position);
        if(vtlPosition == null) {
            Log.error("VTL Position is null");
            return null;
        }

        Vector3d vtlCoord = vtlPosition.getGlobalCoordinates(world, vtlEntity);
        if(vtlCoord == null) {
            Log.error("Cannot convert VTL Position to global coordinates");
            return null;
        }

        Bag<Target> targets = new Bag<>(group.size);

        for(int target = group.iterator(); target != 0; target = group.next()) {
            if(ship == target) {
                // can't shoot itself ...for now
                continue;
            }
            
            Position targetPos = (Position) world.getComponent(target, CompType.Position);
            if(targetPos == null) {
                Log.error("No Position for target: " + ID.get(target));
                continue;
            }

            Vector3d targetCoord = targetPos.getGlobalCoordinates(world, target);
            if(targetCoord == null) {
                Log.error("Cannot convert Position to global coordinates: " + ID.get(target));
                continue;
            }

            // transfer target coordinates to vtl coordinates
            targetCoord = targetCoord.sub(vtlCoord);
            
            CollisionSphere cs = (CollisionSphere) world.getComponent(target, CompType.CollisionSphere);
            double distanceSquared = targetCoord.lengthSquared();
            double closestDistance = Math.sqrt(distanceSquared) - cs.radius;

            if(closestDistance < vtlComponent.range) {
                targets.add(new Target(target, targetCoord, distanceSquared, closestDistance, cs.radius));
            }
        }
        
        targets.sort();
        
        return targets;
    }
}
