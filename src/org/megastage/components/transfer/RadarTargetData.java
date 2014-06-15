package org.megastage.components.transfer;

import org.megastage.ecs.ReplicatedComponent;

public class RadarTargetData extends ReplicatedComponent {
    public int eid;

    public static RadarTargetData create(int target) {
        RadarTargetData data = new RadarTargetData();
        data.eid = target;
        return data;
    }
}
