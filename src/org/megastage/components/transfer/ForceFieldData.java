package org.megastage.components.transfer;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.BaseComponent;
import org.megastage.util.ID;

public class ForceFieldData extends BaseComponent {
    public float energy;
    public float radius;

    @Override
    public void receive(Connection pc, Entity entity) {
        //Log.info(ID.get(entity) + toString());
        super.receive(pc, entity); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    public static ForceFieldData create(float radius, float damage) {
        ForceFieldData data = new ForceFieldData();
        data.radius = radius;
        data.energy = damage;
        return data;
    }
    
    @Override
    public String toString() {
        return "ForceFieldData[radius=" + radius + ", damage="+energy+"]";
    }
}
