package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.Vector;

public class Engine extends DCPUHardware {
    public Vector thrust;
    public char power;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_ENGINE;
        revision = 0x1111;
        manufactorer = MANUFACTORER_GENERAL_ENGINES;

        super.init(world, parent, element);

        double x = element.getAttribute("x").getDoubleValue();
        double y = element.getAttribute("y").getDoubleValue();
        double z = element.getAttribute("z").getDoubleValue();
        thrust = new Vector(-x, -y, -z);
    }

    public void interrupt() {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        Log.debug("a=" + Integer.toHexString(dcpu.registers[0]) + ", b=" + Integer.toHexString(dcpu.registers[1]));

        if (a == 0) {
            power = b;
        } else if (a == 1) {
            dcpu.registers[2] = power;
        }
    }

    public double getPowerLevel() {
        return power / 65535.0d;
    }

    public Vector getCurrentAccelerationVector(double shipMass) {
        double multiplier = getPowerLevel() / shipMass;
        return thrust.multiply(multiplier);
    }

    public boolean isActive() {
        return power != 0;
    }

    public Vector getForce() {
        return thrust.multiply(getPowerLevel());
    }
}
