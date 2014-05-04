package org.megastage.components.srv;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

public class Identifier extends BaseComponent {
    public String name;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        name = element.getAttributeValue("name");
        
        return null;
    }

    @Override
    public Message replicate(int eid) {
        return always(eid);
    }

    @Override
    public String toString() {
        return "Identifier(" + name + ")";
    }
}
