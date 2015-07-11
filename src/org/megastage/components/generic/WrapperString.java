package org.megastage.components.generic;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.util.XmlUtil;

public class WrapperString extends GenericComponent {
    public String value;

    public void config(Element elem) {
        super.config(elem);
        value = XmlUtil.getStringValue(elem, "value");
    }

    public static WrapperString create(int cid, String value) {
        WrapperString me = new WrapperString();
        me.value = value;
        me.cid = cid;
        return me;
    }
}
