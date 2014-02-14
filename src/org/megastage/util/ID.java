package org.megastage.util;

import com.artemis.Entity;
import org.megastage.components.srv.Identifier;

public class ID {
    public static String get(Entity e) {
        if(e == null) return "null ";
        Identifier id = Mapper.IDENTIFIER.get(e);
        if(id==null) {
            return e.toString();
        }
        return id.name + " " + e.toString() + " ";
    }
}
