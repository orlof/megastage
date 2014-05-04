package org.megastage.components;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.World;

public class Mass extends BaseComponent {
    public double mass;

    public Mass() {}
    
    public Mass(double mass) {
        this.mass = mass;
    }

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        mass = getDoubleValue(element, "kg", 0.0);
        
        return null;
    }

    public String toString() {
        return "Mass(" + mass + ")";
    }
}
