package org.megastage.systems.srv;

import org.megastage.util.Log;
import com.jme3.math.Vector3f;
import org.megastage.components.srv.CollisionSphere;
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

public class TargetManagerSystem extends Processor {
    public static TargetManagerSystem INSTANCE;
    
    public TargetManagerSystem(World world, long interval) {
        super(world, interval, CompType.Position, CompType.CollisionSphere);
    }

    public Hit findHit(int vtlEntity, VirtualThermalLaser vtlComponent) {
        int ship = vtlComponent.shipEID;
        Bag<Target> targets = getTargetsInRange(ship, vtlEntity, vtlComponent);

        if(targets == null || targets.isEmpty()) {
            return new NoHit();
        }

        Rotation shipRot = (Rotation) world.getComponent(ship, CompType.Rotation);
        Rotation wpnRot = (Rotation) world.getComponent(vtlEntity, CompType.Rotation);
        
        Vector3f attackVector = shipRot.rotateLocal(wpnRot.rotateLocal(new Vector3f(0.0f, 0.0f, -1.0f)));
        
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

        Vector3f vtlCoord = vtlPosition.getGlobalCoordinates(vtlEntity);
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

            Vector3f targetCoord = targetPos.getGlobalCoordinates(target);
            if(targetCoord == null) {
                Log.error("Cannot convert Position to global coordinates: " + ID.get(target));
                continue;
            }

            // transfer target coordinates to vtl coordinates
            targetCoord.subtractLocal(vtlCoord);
            
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
