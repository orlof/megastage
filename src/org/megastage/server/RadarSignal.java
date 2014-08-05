package org.megastage.server;

import org.megastage.util.ID;

public class RadarSignal implements Comparable<RadarSignal> {
    public int eid;
    public double distanceSquared;

    public RadarSignal(int eid, double distanceSquared) {
        this.eid = eid;
        this.distanceSquared = distanceSquared;
    }

    @Override
    public int compareTo(RadarSignal other) {
        if(distanceSquared < other.distanceSquared) return -1;
        else if(distanceSquared > other.distanceSquared) return 1;
        return 0;
    }

    @Override
    public String toString() {
        return ID.get(eid) + distanceSquared;
    }
}
