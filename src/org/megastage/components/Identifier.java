package org.megastage.components;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;

public class Identifier extends ReplicatedComponent {
    public String name;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        name = element.getAttributeValue("name");
        
        return null;
    }
}
