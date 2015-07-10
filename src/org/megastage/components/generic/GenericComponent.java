package org.megastage.components.generic;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.util.XmlUtil;

public class GenericComponent extends BaseComponent {

    public int cid;

    public void config(Element elem) {
        String type = XmlUtil.getStringValue(elem, "type");
        cid = CompType.getSpec(type).cid;
    }
}
