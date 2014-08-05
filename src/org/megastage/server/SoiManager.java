package org.megastage.server;

import org.megastage.components.Position;
import org.megastage.components.srv.SphereOfInfluence;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Group;
import org.megastage.ecs.World;

public class SoiManager {
    private static Group group;
    
    public static void initialize() {
        group = World.INSTANCE.createGroup(CompType.SphereOfInfluence, CompType.Position);
    }

    public static int getSoi(int ship) {
        Position shipPos = (Position) World.INSTANCE.getComponent(ship, CompType.Position);
        
        float minRadius = Float.MAX_VALUE;
        int selected = 0;

        for(int eid = group.iterator(); eid != 0; eid = group.next()) {
            SphereOfInfluence soi = (SphereOfInfluence) World.INSTANCE.getComponent(eid, CompType.SphereOfInfluence);
            if(soi.radius < minRadius) {
                Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
                if(shipPos.distance(pos) < soi.radius) {
                    minRadius = soi.radius;
                    selected = eid;
                }
            }
        }

        return selected;
    }
}
