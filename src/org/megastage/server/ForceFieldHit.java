package org.megastage.server;

import com.jme3.math.Vector3f;
import org.megastage.util.ID;

public class ForceFieldHit extends Hit {
    public int eid;

    public ForceFieldHit(Target target, Vector3f attackVector) {
        super(target.getImpactDistance(attackVector));
        this.eid = target.eid;
    }

    @Override
    public String toString() {
        return "ForceFieldHit(distance=" + distance + ", entity=" + ID.get(eid) +")";
    }
}
