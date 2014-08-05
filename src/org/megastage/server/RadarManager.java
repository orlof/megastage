package org.megastage.server;

import org.megastage.components.Position;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Group;
import org.megastage.ecs.World;
import org.megastage.util.Bag;

public class RadarManager {
    private static Group group;
    
    public static void initialize() {
        group = World.INSTANCE.createGroup(CompType.Position, CompType.RadarEcho);
    }

    public static Bag<RadarSignal> getRadarSignals(int eid) {
        Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
        if(pos == null) {
            return new Bag<>(0);
        }

        Bag<RadarSignal> signals = new Bag<>(100);
        for(int target = group.iterator(); target != 0; target = group.next()) {
            if(target == eid) continue;

            Position otherPos = (Position) World.INSTANCE.getComponent(target, CompType.Position);

            float distanceSquared = pos.distanceSquared(otherPos);
            signals.add(new RadarSignal(target, distanceSquared));
        }

        signals.sort();

        return signals;
    }
    
    public static int findBySignature(char signature) {
        for(int eid = group.iterator(); eid != 0; eid = group.next()) {
            if(match(eid, signature)) {
                return eid;
            }
        }

        return 0;
    }

    private static boolean match(int eid, char signature) {
        return (char) (eid & 0xffff) == signature;
    }
}
