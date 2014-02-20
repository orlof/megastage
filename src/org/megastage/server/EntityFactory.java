package org.megastage.server;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.esotericsoftware.minlog.Log;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.srv.Identifier;
import org.megastage.components.srv.InitializeFlag;
import org.megastage.components.srv.ReplicateFlag;


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

        try {
            for(Element e: element.getChildren("component")) {
                Class clazz = Class.forName("org.megastage.components." + e.getAttributeValue("type"));
                BaseComponent comp = (BaseComponent) clazz.newInstance();

                BaseComponent[] additionalComponents = comp.init(world, parent, e);
                entity.addComponent(comp);
                Log.info(" Component: " + comp.toString());

                if(additionalComponents != null) {
                    for(BaseComponent c: additionalComponents) {
                        entity.addComponent(c);
                        Log.info(" Component: " + c.toString());
                    }
                }
            }
            entity.addComponent(new InitializeFlag());

            for(Element e: element.getChildren("group")) {
                String groupName = e.getAttributeValue("name");

                world.getManager(GroupManager.class).add(entity, groupName);
                if(groupName.equals("replicate")) {
                    entity.addComponent(new ReplicateFlag());
                }
            }

            for(Element e: element.getChildren("tag")) {
                String tagName = e.getAttributeValue("name");
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
