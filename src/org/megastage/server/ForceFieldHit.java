package org.megastage.server;

import com.jme3.math.Vector3f;
import org.megastage.util.ID;

public class ForceFieldHit extends Hit {
    public int eid;
    public Vector3f contactPoint;

    public ForceFieldHit(Target target) {
        super(target.intersectionDistance);
        this.eid = target.eid;
        this.contactPoint = target.contactPoint;
    }

    @Override
    public String toString() {
        return "ForceFieldHit(distance=" + distance + ", entity=" + ID.get(eid) +")";
    }
}
