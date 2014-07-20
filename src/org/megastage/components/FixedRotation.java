package org.megastage.components;

import org.megastage.ecs.BaseComponent;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;

public class FixedRotation extends ReplicatedComponent {
    public double speed_x, speed_y, speed_z;
    public double period_x, period_y, period_z;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        period_x = 3600.0 * getDoubleValue(element, "period_x", 0.0);
        speed_x = period_x == 0.0 ? 0.0: ((2.0 * Math.PI) / (1000.0 * period_x));
        period_y = 3600.0 * getDoubleValue(element, "period_y", 0.0);
        speed_y = period_y == 0.0 ? 0.0: ((2.0 * Math.PI) / (1000.0 * period_y));
        period_z = 3600.0 * getDoubleValue(element, "period_z", 0.0);
        speed_z = period_z == 0.0 ? 0.0: ((2.0 * Math.PI) / (1000.0 * period_z));
        
        return null;
    }

    public double getX(long time) {
        return (time * speed_x)  % (2.0 * Math.PI);
    }
    public double getY(long time) {
        return (time * speed_y)  % (2.0 * Math.PI);
    }
    public double getZ(long time) {
        return (time * speed_z)  % (2.0 * Math.PI);
    }
}
