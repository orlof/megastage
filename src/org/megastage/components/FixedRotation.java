package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import static org.megastage.components.BaseComponent.getDoubleValue;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class FixedRotation extends BaseComponent {
    public double speed_x, speed_y, speed_z;
    public double period_x, period_y, period_z;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        period_x = 3600.0 * getDoubleValue(element, "period_x", 0.0);
        speed_x = period_x == 0.0 ? 0.0: ((2.0 * Math.PI) / (1000.0 * period_x));
        period_y = 3600.0 * getDoubleValue(element, "period_y", 0.0);
        speed_y = period_y == 0.0 ? 0.0: ((2.0 * Math.PI) / (1000.0 * period_y));
        period_z = 3600.0 * getDoubleValue(element, "period_z", 0.0);
        speed_z = period_z == 0.0 ? 0.0: ((2.0 * Math.PI) / (1000.0 * period_z));
        
        return null;
    }

    @Override
    public boolean replicate() {
        return true;
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
    
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("FixedRotation(");
        sb.append("period_x=").append(period_x);
        sb.append(", period_y=").append(period_y);
        sb.append(", period_z=").append(period_z);

        sb.append(", speed_x=").append(speed_x);
        sb.append(", speed_y=").append(speed_y);
        sb.append(", speed_z=").append(speed_z);
        sb.append(")");
        
        return sb.toString();
    }
}
