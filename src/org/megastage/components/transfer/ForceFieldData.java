package org.megastage.components.transfer;

import org.megastage.components.BaseComponent;

public class ForceFieldData extends BaseComponent {
    public float radius;

    public static ForceFieldData create(float radius) {
        ForceFieldData data = new ForceFieldData();
        data.radius = radius;
        return data;
    }
    
    @Override
    public String toString() {
        return "ForceFieldData[radius=" + radius + "]";
    }
}
