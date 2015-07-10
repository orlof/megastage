package org.megastage.server;

import org.jdom2.Element;
import org.megastage.components.Identifier;
import org.megastage.components.generic.Flag;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.Log;

public class EntityFactory {
    public static int create(Element element) {
        int eid = World.INSTANCE.createEntity();
        
        Identifier id = new Identifier();
        id.name = element.getAttributeValue("name");
        World.INSTANCE.setComponent(eid, CompType.Identifier, id);

        Log.info(id.toString() + "[" + eid + "]");

        for(Element e: element.getChildren("component")) {
            BaseComponent comp = BaseComponent.create(e);

            Log.info(" Component: " + comp.toString());
            World.INSTANCE.setComponent(eid, comp);
        }
        World.INSTANCE.setComponent(eid, CompType.FlagInitialize, new Flag());

        return eid;
    }
}
