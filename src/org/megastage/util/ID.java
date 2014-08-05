package org.megastage.util;

import org.megastage.components.Identifier;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class ID {
    public static String get(int eid) {
        StringBuilder sb = new StringBuilder(100);
        sb.append("[").append(eid).append("]");

        Identifier id = (Identifier) World.INSTANCE.getComponent(eid, CompType.Identifier);
        if(id != null) {
            sb.append(id.toString());
        }
        
        return sb.toString();
    }
}
