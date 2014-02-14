/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.server;

import com.artemis.Entity;
import com.artemis.managers.Manager;
import java.util.HashMap;
import org.jdom2.Element;

/**
 *
 * @author Orlof
 */
public class TemplateManager extends Manager {
    private HashMap<String, Element> templates = new HashMap<>();
    
    @Override
    public void initialize() {
    }

    public void addTemplate(Element elem) {
        String name = elem.getAttributeValue("name");
        templates.put(name, elem);
    }

    public Entity create(String name) {
        Element element = templates.get(name);
        if(element == null) {
            throw new RuntimeException("No template: " + name + " in " + templates.toString());
        }
        return EntityFactory.create(world, element, null);
    }    

    @Override
    public void dispose() {
    }
}
