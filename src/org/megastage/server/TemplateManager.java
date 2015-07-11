package org.megastage.server;

import java.util.HashMap;
import org.jdom2.Element;
import org.megastage.components.generic.Flag;
import org.megastage.components.generic.WrapperString;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.CompSpec;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.Log;
import org.megastage.util.XmlUtil;

public class TemplateManager {
    private static final HashMap<String, Element> templates = new HashMap<>();
    public static final HashMap<String, Integer> resolver = new HashMap<>();

    public static void initialize(Element templates) {
        for(Element element: templates.getChildren("template")) {
            addTemplate(element);
        }
    }

    public static void addTemplate(Element elem) {
        String name = elem.getAttributeValue("name");
        templates.put(name, elem);
    }

    public static int createTemplate(String templateName) {
        Element element = templates.get(templateName);
        if(element == null) {
            throw new RuntimeException("No template: " + templateName + " in " + templates.toString());
        }

        resolver.clear();

        int eid = 0;
        for(Element elem: element.getChildren("entity")) {
            if(XmlUtil.getBooleanValue(elem, "root", false)) {
                eid = addEntity(element);
            } else {
                addEntity(element);
            }
        }

        return eid;
    }

    private static int addEntity(Element element) {
        int eid = World.INSTANCE.createEntity();

        String name = element.getAttributeValue("name");
        resolver.put(name, eid);

        WrapperString id  = WrapperString.create(eid, name);
        World.INSTANCE.setComponent(eid, CompType.Identifier, id);

        Log.info(id.toString() + "[" + eid + "]");

        for(Element e: element.getChildren("component")) {
            CompSpec spec = CompType.getSpec(element.getName());

            BaseComponent comp = BaseComponent.create(spec, e);
            Log.info(" Component: " + comp.toString());

            World.INSTANCE.setComponent(eid, spec.cid, comp);
        }
        World.INSTANCE.setComponent(eid, CompType.FlagInitialize, new Flag());

        return eid;
    }
}
