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
public class Mass extends EntityComponent {
    public double mass;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        mass = getDoubleValue(element, "kg", 0.0);
        
        return null;
    }

    public String toString() {
        return "Mass(" + mass + ")";
    }
}
