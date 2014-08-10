package org.megastage.systems.srv;

import org.megastage.util.Log;
import com.jme3.math.Vector3f;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.srv.VectorAttack;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.Group;
import org.megastage.ecs.World;
import org.megastage.server.Hit;
import org.megastage.server.NoHit;
import org.megastage.server.Target;
import org.megastage.util.Bag;

public class TargetManager {
    private static Group group;
    
    public static void initialize() {
        group = World.INSTANCE.createGroup(CompType.Position, CompType.CollisionSphere);
    }

    public static Hit vectorAttackHit(int eid) throws ECSException {
        Vector3f wpnPos = Position.getWorldCoordinates(eid);
        Vector3f attVec = Rotation.getWorldRotation(eid).multLocal(VectorAttack.getVector(eid));
 
        Bag<Target> targets = findAllHits(wpnPos, attVec);
        
        if(targets.isEmpty()) {
            return NoHit.INSTANCE;
        }

        Hit bestHit = NoHit.INSTANCE;
        for(Target target: targets) {
            if(!target.canHitCloserThan(bestHit.distance)) {
                return bestHit;
            }

            Hit hit = Hit.create(target);

            if(hit.distance < bestHit.distance) {
                bestHit = hit;
            }
        }

        return bestHit;
    }

    private static Bag<Target> findAllHits(Vector3f wpnPos, Vector3f attVec) {
        Bag<Target> targets = new Bag<>(group.size);
        
        Target target = new Target();
        target.setWeaponPosition(wpnPos);
        target.setAttackVector(attVec);
        
        for(int tgtEid = group.iterator(); tgtEid != 0; tgtEid = group.next()) {
            try {
                if(target.check(tgtEid)) {
                    targets.add(target.clone());
                }
            } catch(ECSException ex) {
                Log.warn(ex);
            }
        }

        targets.sort();

        return targets;
    }
    
}
