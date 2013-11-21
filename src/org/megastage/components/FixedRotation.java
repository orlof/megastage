package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import static org.megastage.components.BaseComponent.getDoubleValue;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.util.Globals;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class FixedRotation extends EntityComponent {
    public double speed_x, speed_y, speed_z;
    public double period_x, period_y, period_z;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        period_x = 3600.0 * getDoubleValue(element, "period_x", 0.0);
        speed_x = ((2.0 * Math.PI) / (1000.0 * period_x));
        period_y = 3600.0 * getDoubleValue(element, "period_y", 0.0);
        speed_y = ((2.0 * Math.PI) / (1000.0 * period_y));
        period_z = 3600.0 * getDoubleValue(element, "period_z", 0.0);
        speed_z = ((2.0 * Math.PI) / (1000.0 * period_z));
    }

    public double getX() {
        return (Globals.time * speed_x)  % (2.0 * Math.PI);
    }
    public double getY() {
        return (Globals.time * speed_y)  % (2.0 * Math.PI);
    }
    public double getZ() {
        return (Globals.time * speed_z)  % (2.0 * Math.PI);
    }
}
