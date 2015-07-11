package org.megastage.components.generic;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.server.TemplateManager;
import org.megastage.util.XmlUtil;

public class EntityReference extends BaseComponent {
    public int ref;

    public void config(Element elem) {
        ref = TemplateManager.resolver.get(XmlUtil.getStringValue(elem, "name"));
    }

    public void setEid(int eid) {
        if(this.ref != eid) {
            this.ref = eid;
            this.dirty = true;
        }
    }
}
