package org.megastage.components;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.World;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Energy extends BaseComponent {
    public double kws;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        kws = getDoubleValue(element, "energy", 0.0);
        
        return null;
    }

    @Override
    public String toString() {
        return "Energy{" + "kws=" + kws + '}';
    }
}
