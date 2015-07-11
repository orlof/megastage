package org.megastage.components;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.util.XmlUtil;

public class PlayerCharacter extends BaseComponent {
    public String name;
    public boolean allocated;

    @Override
    public void config(Element elem) {
        name = XmlUtil.getStringValue(elem, "name");
    }
}
