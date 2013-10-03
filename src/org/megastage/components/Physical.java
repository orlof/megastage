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
public class Physical extends BaseComponent {
    public double mass, radius;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        radius = 1000000 * element.getAttribute("radius_km").getDoubleValue();
        mass = element.getAttribute("mass_kg").getDoubleValue();
    }
}
