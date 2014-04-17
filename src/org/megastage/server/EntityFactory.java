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
    public static Entity create(World world, Element element, Entity parent) throws Exception {
        Entity entity = world.createEntity();
        entity.addToWorld();
        
        Identifier id = new Identifier();
        id.name = element.getAttributeValue("name");
        entity.addComponent(id);

        Log.info(entity.toString());

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

        entity.changedInWorld();

        for(Element e: element.getChildren("entity")) {
            create(world, e, entity);
        }

        return entity;
    }
}
