package org.megastage.components.transfer;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.BaseComponent;
import org.megastage.util.ID;

public class ThermalLaserData extends BaseComponent {
    public char status;
    public char wattage;
    public float range;

    @Override
    public void receive(Connection pc, Entity entity) {
        Log.info(ID.get(entity) + toString());
        super.receive(pc, entity); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static ThermalLaserData create(char status, char wattage, float range) {
        ThermalLaserData tld = new ThermalLaserData();
        tld.status = status;
        tld.wattage = wattage;
        tld.range = range;
        return tld;
    }
    
    @Override
    public String toString() {
        return "ThermalLaserData[status=" + (int) status +", wattage=" + (int) wattage + ", range="+ range + "]";
    }
}
