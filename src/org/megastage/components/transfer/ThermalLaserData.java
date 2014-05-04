package org.megastage.components.transfer;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.World;

public class ThermalLaserData extends BaseComponent {
    // TODO this should contain status, w, Hit, range
    public char status;
    public char wattage;
    public float range;

    @Override
    public void receive(World world, Connection pc, int eid) {
        //Log.info(ID.get(eid) + toString());
        super.receive(world, pc, eid);
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
