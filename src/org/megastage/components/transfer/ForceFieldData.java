package org.megastage.components.transfer;

import org.megastage.components.BaseComponent;

public class ForceFieldData extends BaseComponent {
    public float energy;
    public float radius;

    public static ForceFieldData create(float radius, float damage) {
        ForceFieldData data = new ForceFieldData();
        data.radius = radius;
        data.energy = damage;
        return data;
    }

    @Override
    public String toString() {
        return "ForceFieldData{" + "energy=" + energy + ", radius=" + radius + '}';
    }
}
