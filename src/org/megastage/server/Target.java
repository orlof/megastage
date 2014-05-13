package org.megastage.server;

import org.megastage.components.dcpu.VirtualForceField;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.ID;
import org.megastage.util.Vector3d;

public class Target implements Comparable<Target> {
    public int eid;
    public Vector3d coord;
    public double closestDistance;
    public double collisionRadius;
    private double distanceFromLOF;
    private double distanceSquared;

    public Target() {
    }

    public Target(int eid, Vector3d coord, double distanceSquared, double closestDistance, double colrad) {
        this.eid = eid;
        this.coord = coord;
        this.distanceSquared = distanceSquared;
        this.closestDistance = closestDistance;
        this.collisionRadius = colrad;
    }

    public boolean vtlIsInCollisionSphere() {
        return this.closestDistance < 0.0;
    }

    public boolean isBehind(Vector3d ray) {
        return ray.x * coord.x <= 0 && ray.y * coord.y <= 0 && ray.z * coord.z <= 0;
    }

    public void setDistanceFromLOF(Vector3d attackVector) {
        this.distanceFromLOF = attackVector.distanceToPoint(coord);
    }

    public boolean isInLOF() {
        return distanceFromLOF <= collisionRadius;
    }

    public boolean isShip(World world) {
        Object comp = world.getComponent(eid, CompType.Geometry);
        return comp != null && comp instanceof ShipGeometry;
    }

    public boolean isForceField(World world) {
        return world.hasComponent(eid, CompType.VirtualForceField);
    }

    public double getImpactDistance(Vector3d attackVector) {
        double distanceFromLOFSquared = distanceFromLOF * distanceFromLOF;
        double side = Math.sqrt(distanceSquared - distanceFromLOFSquared);
        side -= Math.sqrt(collisionRadius*collisionRadius - distanceFromLOFSquared);
        return side;
    }

    @Override
    public String toString() {
        return "Target{" + "entity=" + ID.get(eid) + ", coord=" + coord + ", closestDistance=" + closestDistance + ", collisionRadius=" + collisionRadius + ", distanceFromLOF=" + distanceFromLOF + ", distanceSquared=" + distanceSquared + '}';
    }

    @Override
    public int compareTo(Target o) {
        return Double.compare(closestDistance, o.closestDistance);
    }
}
