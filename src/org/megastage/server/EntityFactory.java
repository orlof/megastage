package org.megastage.server;

import com.esotericsoftware.minlog.Log;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.srv.Identifier;
import org.megastage.components.srv.InitializeFlag;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class EntityFactory {
    public static int create(World world, Element element, int parentEid) throws Exception {
        int eid = world.createEntity();
        
        Identifier id = new Identifier();
        id.name = element.getAttributeValue("name");
        world.addComponent(eid, CompType.Identifier, id);

        Log.info(eid + " " + id.toString());

        for(Element e: element.getChildren("component")) {
            Class clazz = Class.forName("org.megastage.components." + e.getAttributeValue("type"));
            BaseComponent comp = (BaseComponent) clazz.newInstance();
            BaseComponent[] additionalComponents = comp.init(world, parentEid, e);

            Log.info(" Component: " + comp.toString());
            world.addComponent(eid, CompType.cid(comp.getClass().getSimpleName()), comp);

            if(additionalComponents != null) {
                for(BaseComponent c: additionalComponents) {
                    Log.info(" Component: " + c.toString());
                    world.addComponent(eid, CompType.cid(c.getClass().getSimpleName()), c);
                }
            }
        }
        world.addComponent(eid, CompType.InitializeFlag, new InitializeFlag());

        for(Element e: element.getChildren("entity")) {
            create(world, e, eid);
        }

        return eid;
    }
}
