package org.megastage.components.transfer;

import org.megastage.ecs.BaseComponent;

public class RadarTargetData extends BaseComponent {
    public int eid;

    public static RadarTargetData create(int target) {
        RadarTargetData data = new RadarTargetData();
        data.eid = target;
        return data;
    }
}
