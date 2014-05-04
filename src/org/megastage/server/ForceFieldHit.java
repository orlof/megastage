package org.megastage.server;

import org.megastage.util.ID;
import org.megastage.util.Vector3d;

public class ForceFieldHit extends Hit {
    public int eid;

    public ForceFieldHit(Target target, Vector3d attackVector) {
        super(target.getImpactDistance(attackVector));
        this.eid = target.eid;
    }

    public String toString() {
        return "ForceFieldHit(distance=" + distance + ", entity=" + ID.get(eid) +")";
    }
}
