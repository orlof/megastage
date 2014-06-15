package org.megastage.components.transfer;

import org.megastage.ecs.ReplicatedComponent;

public class EngineData extends ReplicatedComponent {
    public char power = 0;
    
    public static EngineData create(char power) {
        EngineData data = new EngineData();
        data.power = power;
        return data;
    }
}
