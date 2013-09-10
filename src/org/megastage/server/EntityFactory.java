package org.megastage.server;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

import java.util.logging.Logger;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:01
 */
public class EntityFactory {
    private final static Logger LOG = Logger.getLogger(EntityFactory.class.getName());

    public static Entity create(World world, Element element, Entity parent) {
        Entity entity = world.createEntity();

        LOG.info(entity.toString());
        if(parent != null) LOG.finer("Parent[" + parent.getId() + "]");

        try {
            for(Element e: element.getChildren("component")) {
                LOG.finer("Add Component of type " + e.getAttributeValue("type"));

                Class clazz = Class.forName("org.megastage.components." + e.getAttributeValue("type"));
                BaseComponent comp = (BaseComponent) clazz.newInstance();

                comp.init(world, parent, e);
                entity.addComponent(comp);
            }

            for(Element e: element.getChildren("group")) {
                String groupName = e.getAttributeValue("name");
                LOG.finer("Add to group " + groupName);

                world.getManager(GroupManager.class).add(entity, groupName);
            }

            for(Element e: element.getChildren("tag")) {
                String tagName = e.getAttributeValue("name");
                LOG.finer("Tag with " + tagName);

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
