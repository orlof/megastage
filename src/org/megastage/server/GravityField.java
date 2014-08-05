package org.megastage.server;

public class GravityField implements Comparable<GravityField> {
    public int eid;
    public double field;

    public GravityField(int eid, double gravitationalField) {
        this.eid = eid;
        this.field = gravitationalField;
    }

    @Override
    public int compareTo(GravityField o) {
        return field == o.field ? 0: field < o.field ? -1: +1;
    }
}
