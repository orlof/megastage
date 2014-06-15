package org.megastage.components.transfer;

import org.megastage.ecs.ReplicatedComponent;

public class GyroscopeData extends ReplicatedComponent {
    public char power = 0;
    
    public static GyroscopeData create(char power) {
        GyroscopeData data = new GyroscopeData();
        data.power = power;
        return data;
    }

    public float getAngularSpeed() {
        return (power < 0x8000 ? (float) power: power - 65536f) / 32767f;
    }
}
