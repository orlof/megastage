package org.megastage.components.transfer;

import org.megastage.ecs.ReplicatedComponent;

public class GyroscopeData extends ReplicatedComponent {
    public int signedValue = 0;
    
    public static GyroscopeData create(int signedValue) {
        GyroscopeData data = new GyroscopeData();
        data.signedValue = signedValue;
        return data;
    }

    public float getAngularSpeed() {
        return signedValue / 32767.0f;
    }
}
