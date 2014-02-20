/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.transfer;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.components.BaseComponent;
import org.megastage.util.Mapper;

public class GyroscopeData extends BaseComponent {
    public char power = 0;

    @Override
    public void receive(Connection pc, Entity entity) {
        GyroscopeData data = Mapper.GYROSCOPE_DATA.get(entity);
        if(data == null) {
            entity.addComponent(this);
            entity.changedInWorld();
        } else {
            data.power = power;
        }
    }
    
    public float getAngularSpeed() {
        return (power < 0x8000 ? (float) power: power - 65536f) / 32767f;
    }
}
