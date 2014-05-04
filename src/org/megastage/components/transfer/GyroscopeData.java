package org.megastage.components.transfer;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class GyroscopeData extends BaseComponent {
    public char power = 0;

    @Override
    public void receive(World world, Connection pc, int eid) {
        GyroscopeData data = (GyroscopeData) world.getComponent(eid, CompType.GyroscopeData);
        if(data == null) {
            world.addComponent(eid, CompType.GyroscopeData, this);
        } else {
            data.power = power;
        }
    }
    
    public float getAngularSpeed() {
        return (power < 0x8000 ? (float) power: power - 65536f) / 32767f;
    }
}
