/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.server;

import java.util.HashMap;
import org.jdom2.Element;
import org.megastage.ecs.World;

/**
 *
 * @author Orlof
 */
public class TemplateManager {
    private static HashMap<String, Element> templates = new HashMap<>();
    
    public static void addTemplate(Element elem) {
        String name = elem.getAttributeValue("name");
        templates.put(name, elem);
    }

    public static int create(World world, String name) throws Exception {
        Element element = templates.get(name);
        if(element == null) {
            throw new RuntimeException("No template: " + name + " in " + templates.toString());
        }
        return EntityFactory.create(world, element, 0);
    }    
}
