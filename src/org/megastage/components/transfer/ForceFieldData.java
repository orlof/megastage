package org.megastage.components.transfer;

import org.megastage.components.BaseComponent;
import org.megastage.components.dcpu.VirtualForceField;

public class ForceFieldData extends BaseComponent {
    public float radius;
    public char status;

    public ForceFieldData() {
    }
    
    public static ForceFieldData create(float radius, char status) {
        ForceFieldData data = new ForceFieldData();
        data.radius = radius;
        data.status = status;
        return data;
    }
    
    public boolean isVisible() {
        return status == VirtualForceField.STATUS_FIELD_ACTIVE;
    }

    @Override
    public String toString() {
        return "ForceFieldData{" + "radius=" + radius + ", status=" + (int) status + '}';
    }

}
