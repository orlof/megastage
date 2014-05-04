package org.megastage.server;

import org.megastage.components.Position;
import org.megastage.components.srv.SphereOfInfluence;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.ID;
import org.megastage.util.Vector3d;

public class SoiData implements Comparable<SoiData> {
    public final int eid;

    public final double radius;
    public final int priority;
    public final Vector3d coord;

    public SoiData(World world, int eid) {
        this.eid = eid;

        Position pos = (Position) world.getComponent(eid, CompType.Position);
        this.coord = pos.getVector3d();

        SphereOfInfluence soi = (SphereOfInfluence) world.getComponent(eid, CompType.SphereOfInfluence);
        this.radius = soi.radius;
        this.priority = soi.priority;
    }

    public boolean contains(Vector3d coord) {
        return priority == -1 || coord.distance(this.coord) < radius;
    }

    @Override
    public String toString() {
        return "SoiData(entity="+ID.get(eid)+", coord=" + coord.toString() + ", radius=" + radius +", priority=" + priority +")";
    }

    @Override
    public int compareTo(SoiData o) {
        return o.priority - priority;
    }
}
