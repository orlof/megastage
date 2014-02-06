package org.megastage.components.srv;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

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

    @Override
    public boolean replicate() {
        return true;
    }

    public String name;

    public String toString() {
        return "Identifier(" + name + ")";
    }
}
