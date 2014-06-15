package org.megastage.components;

import org.megastage.ecs.BaseComponent;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;

public class Mass extends ReplicatedComponent {
    public double mass;

    public static Mass create(double m) {
        Mass mass = new Mass();
        mass.mass = m;
        return mass;
    }

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        mass = getDoubleValue(element, "kg", 0.0);
        
        return null;
    }
}
