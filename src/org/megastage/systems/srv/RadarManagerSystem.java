package org.megastage.systems.srv;

import org.megastage.components.Position;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.server.RadarSignal;
import org.megastage.util.Bag;
import org.megastage.util.Vector3d;

public class RadarManagerSystem extends Processor {
    public static RadarManagerSystem INSTANCE;
    
    public RadarManagerSystem(World world, long interval) {
        super(world, interval, CompType.Mass, CompType.Position, CompType.RadarEcho);
        INSTANCE = this;
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }

    public Bag<RadarSignal> getRadarSignals(int eid) {
        Position pos = (Position) world.getComponent(eid, CompType.Position);
        if(pos == null) {
            return new Bag<>(0);
        }

        Vector3d coord = pos.getVector3d();
        
        Bag<RadarSignal> signals = new Bag<>(100);
        for(int target = group.iterator(); target != 0; target = group.next()) {
            if(target == eid) continue;

            // System that stores targets checks for Position existence
            Position targetPos = (Position) world.getComponent(target, CompType.Position);
            Vector3d targetPosVec = targetPos.getVector3d();

            double distanceSquared = coord.distanceSquared(targetPosVec);
            signals.add(new RadarSignal(target, distanceSquared));
        }

        signals.sort();

        return signals;
    }
    
    public int findBySignature(char signature) {
        for(int eid = group.iterator(); eid != 0; eid = group.next()) {
            if(match(eid, signature)) {
                return eid;
            }
        }
        return 0;
    }

    public static boolean match(int eid, char signature) {
        return (char) (eid & 0xffff) == signature;
    }
}
