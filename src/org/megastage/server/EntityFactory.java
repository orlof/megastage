package org.megastage.server;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.esotericsoftware.minlog.Log;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.Identifier;


/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:01
 */
public class EntityFactory {
    public static Entity create(World world, Element element, Entity parent) {
        Entity entity = world.createEntity();
        
        Identifier id = new Identifier();
        id.name = element.getAttributeValue("name");
        entity.addComponent(id);

        Log.info(entity.toString());
        if(parent != null) Log.debug("Parent[" + parent.getId() + "]");

        try {
            for(Element e: element.getChildren("component")) {
                Class clazz = Class.forName("org.megastage.components." + e.getAttributeValue("type"));
                BaseComponent comp = (BaseComponent) clazz.newInstance();

                comp.init(world, parent, e);
                entity.addComponent(comp);

                Log.debug("Add Component: " + comp.toString());
            }

            for(Element e: element.getChildren("group")) {
                String groupName = e.getAttributeValue("name");
                Log.debug("Add to group " + groupName);

                world.getManager(GroupManager.class).add(entity, groupName);
            }

            for(Element e: element.getChildren("tag")) {
                String tagName = e.getAttributeValue("name");
                Log.debug("Tag with " + tagName);

                world.getManager(TagManager.class).register(tagName, entity);
            }

            entity.addToWorld();

            for(Element e: element.getChildren("entity")) {
                create(world, e, entity);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        return entity;
    }
}
