package org.megastage.systems.srv;

import org.megastage.components.Position;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.server.SoiData;
import org.megastage.util.Bag;
import org.megastage.util.Vector3d;

public class SoiManagerSystem extends Processor {
    public static SoiManagerSystem INSTANCE;

    public SoiManagerSystem(World world, long interval) {
        super(world, interval, CompType.SphereOfInfluence, CompType.Position);
        INSTANCE = this;
    }

    private Bag<SoiData> soiData = new Bag<>(0);

    protected void process() {
        Bag<SoiData> tmpData = new Bag<>(group.size);
        
        for(int eid = group.iterator(); eid != 0; eid = group.next()) {
            tmpData.add(new SoiData(world, eid));
        }
        
        tmpData.sort();
        
        soiData = tmpData;
    }
    
    public SoiData getSoi(int shipEid) {
        Position pos = (Position) world.getComponent(shipEid, CompType.Position);
        Vector3d coord = pos.getVector3d();

        for(SoiData data: soiData) {
            if(data.contains(coord)) {
                return data;
            }
        }
        return null;
    }
}
