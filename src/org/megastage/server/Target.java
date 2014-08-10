package org.megastage.server;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import org.megastage.components.Position;
import org.megastage.components.srv.CollisionSphere;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ECSException;
import org.megastage.ecs.World;

public class Target implements Comparable<Target>, Cloneable {
    public Vector3f weaponPosition;
    public Vector3f attackVector;

    public int eid;
    public float intersectionDistance;

    private float distanceFromTargetToAttackVector;
    private float t;

    @Override
    public int compareTo(Target o) {
        return Double.compare(intersectionDistance, o.intersectionDistance);
    }

    public void setWeaponPosition(Vector3f wpnPos) {
        this.weaponPosition = wpnPos;
    }

    public void setAttackVector(Vector3f attVec) {
        this.attackVector = attVec;
    }

    public void calcDistanceToAttackVector() throws ECSException {
        Vector3f targetPosition = Position.getWorldCoordinates(eid).subtract(weaponPosition);
        
        float l2 = attackVector.lengthSquared();
        if(l2 < 0.01f) {
            t = 0.0f;
            distanceFromTargetToAttackVector = targetPosition.length();
            return;
        }
        
        t = targetPosition.dot(attackVector) / l2;
        if (t < 0.0f) {
            distanceFromTargetToAttackVector = targetPosition.length();
        } else if (t > 1.0f) {
            distanceFromTargetToAttackVector = targetPosition.distance(attackVector);
        } else {
            distanceFromTargetToAttackVector = targetPosition.distance(attackVector.mult(t));
        }
    }

    public boolean calcIntersectionDistance() throws ECSException {
        float targetCollisionRadius = CollisionSphere.getRadius(eid);

        if(targetCollisionRadius > distanceFromTargetToAttackVector) {
            float thc = FastMath.sqrt(targetCollisionRadius * targetCollisionRadius - distanceFromTargetToAttackVector * distanceFromTargetToAttackVector);
            float tca = attackVector.length() * t;
            intersectionDistance = tca - thc;
            return true;
        }
        
        return false;
    }
    
    public boolean canHitCloserThan(float distance) {
        return intersectionDistance < distance;
    }
    
    public boolean isShip() {
        return World.INSTANCE.hasComponent(eid, CompType.ShipGeometry);
    }

    public boolean isForceField() {
        return World.INSTANCE.hasComponent(eid, CompType.VirtualForceField);
    }    
    
    @Override
    public Target clone() {
        try {
            return (Target) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // can not happen
        }
    }

    public boolean check(int eid) throws ECSException {
        this.eid = eid;
        
        calcDistanceToAttackVector();

        return calcIntersectionDistance();
    }
}
