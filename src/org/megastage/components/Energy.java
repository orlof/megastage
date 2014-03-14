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
public class Energy extends BaseComponent {
    public double kws;

    public Energy() {}
    
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        kws = getDoubleValue(element, "energy", 0.0);
        
        return null;
    }

    @Override
    public String toString() {
        return "Energy{" + "kws=" + kws + '}';
    }
}
