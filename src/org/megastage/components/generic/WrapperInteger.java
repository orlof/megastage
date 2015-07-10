package org.megastage.components.generic;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.server.EntityResolver;
import org.megastage.util.XmlUtil;

public class WrapperInteger extends GenericComponent {

    public int value;

    public void config(Element elem) {
        super.config(elem);
        value = XmlUtil.getIntegerValue(elem, "value");
    }
}
