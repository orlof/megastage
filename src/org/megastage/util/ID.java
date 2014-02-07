/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.util;

import com.artemis.Entity;
import org.megastage.components.srv.Identifier;

public class ID {
    public static String get(Entity e) {
        if(e == null) return "null ";
        Identifier id = e.getComponent(Identifier.class);
        if(id==null) {
            return e.toString();
        }
        return id.name + " " + e.toString() + " ";
    }
}
