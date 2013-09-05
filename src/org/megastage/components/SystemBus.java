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
public class SystemBus extends BaseComponent {
    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        address = element.getAttribute("address").getIntValue();
    }
    
    public int address;
}
