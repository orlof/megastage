package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Identifier extends BaseComponent {
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        name = element.getAttributeValue("name");
        
        return null;
    }
    
    public String name;

    public String toString() {
        return "Identifier(" + name + ")";
    }
}
