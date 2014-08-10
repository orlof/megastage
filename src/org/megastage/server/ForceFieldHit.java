package org.megastage.server;

import org.megastage.util.ID;

public class ForceFieldHit extends Hit {
    public int eid;

    public ForceFieldHit(Target target) {
        super(target.intersectionDistance);
        this.eid = target.eid;
    }

    @Override
    public String toString() {
        return "ForceFieldHit(distance=" + distance + ", entity=" + ID.get(eid) +")";
    }
}
