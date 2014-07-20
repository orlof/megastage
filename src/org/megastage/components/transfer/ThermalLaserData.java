package org.megastage.components.transfer;

import org.megastage.ecs.ReplicatedComponent;

public class ThermalLaserData extends ReplicatedComponent {
    // TODO this should contain status, w, Hit, range
    public char status;
    public char wattage;
    public float range;

    public static ThermalLaserData create(char status, char wattage, float range) {
        ThermalLaserData tld = new ThermalLaserData();
        tld.status = status;
        tld.wattage = wattage;
        tld.range = range;
        return tld;
    }
}
