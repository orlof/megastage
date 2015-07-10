package org.megastage.components.transfer;

import org.megastage.components.dcpu.VirtualForceField;
import org.megastage.ecs.BaseComponent;

public class ForceFieldData extends BaseComponent {
    public float radius;
    public char status;

    public static ForceFieldData create(float radius, char status) {
        ForceFieldData data = new ForceFieldData();
        data.radius = radius;
        data.status = status;
        return data;
    }
    
    public boolean isVisible() {
        return status == VirtualForceField.STATUS_FIELD_ACTIVE;
    }
}
