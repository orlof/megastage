package org.megastage.components.dcpu;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.server.TemplateManager;
import org.megastage.util.XmlUtil;

public class DCPUConnection extends BaseComponent {
    public int eid;

    public void config(Element elem) {
        eid = TemplateManager.resolver.get(XmlUtil.getStringValue(elem, "name"));
    }
}
