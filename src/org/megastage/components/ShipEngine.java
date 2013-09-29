package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.Vector;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class ShipEngine extends BaseComponent {
    public Entity ship;
    public int address;
    public Vector thrust;
    public double power;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        ship = parent;

        address = element.getAttribute("address").getIntValue();

        double x = element.getAttribute("x").getDoubleValue();
        double y = element.getAttribute("y").getDoubleValue();
        double z = element.getAttribute("z").getDoubleValue();
        thrust = new Vector(x, y, z);
    }


    public Vector getAcceleration(double shipMass) {
        double multiplier = power / shipMass;
        return thrust.multiply(multiplier);
    }
}
