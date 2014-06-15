package org.megastage.util;

import org.megastage.components.Identifier;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class ID {
    public static String get(int eid) {
        Identifier id = (Identifier) World.INSTANCE.getComponent(eid, CompType.Identifier);
        if(id==null) {
            return "[" + eid + "]";
        }

        return id.toString() + "[" + eid + "]";
    }
}
