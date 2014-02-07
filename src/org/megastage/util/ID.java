/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.util;

import com.artemis.Entity;
import org.megastage.components.srv.Identifier;

/**
 *
 * @author Teppo
 */
public class ID {
    public static String get(Entity e) {
        Identifier id = e.getComponent(Identifier.class);
        if(id==null) {
            return e.toString();
        }
        return id.name + " " + e.toString() + " ";
    }
}
