package org.megastage.components.generic;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.server.EntityResolver;
import org.megastage.util.XmlUtil;

public class EntityReference extends GenericComponent {

    public int eid;

    public void config(Element elem) {
        super.config(elem);
        eid = EntityResolver.resolve(XmlUtil.getStringValue(elem, "name"));
    }
}
