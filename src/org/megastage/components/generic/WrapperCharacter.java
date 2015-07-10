package org.megastage.components.generic;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.util.XmlUtil;

public class WrapperCharacter extends GenericComponent {
    public char value;

    public void config(Element elem) {
        super.config(elem);
        value = XmlUtil.getCharacterValue(elem, "value");
    }

    public static WrapperCharacter create(int cid, char value) {
        WrapperCharacter me = new WrapperCharacter();
        me.value = value;
        me.cid = cid;
        return me;
    }
}
