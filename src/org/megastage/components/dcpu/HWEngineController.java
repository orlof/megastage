package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.RAM;
import org.megastage.util.Vector;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class HWEngineController extends DCPUHardware {
    private final static Logger LOG = Logger.getLogger(HWEngineController.class.getName());

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
        thrust = new Vector(x, y, z);
    }

    public void interrupt() {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        LOG.fine("a=" + Integer.toHexString(dcpu.registers[0]) + ", b=" + Integer.toHexString(dcpu.registers[1]));

        if (a == 0) {
            power = b;
        } else if (a == 1) {
            dcpu.registers[2] = power;
        }
    }

    public double getPower() {
        return power / 65535.0d;
    }

    public Vector getCurrentAccelerationVector(double shipMass) {
        double multiplier = getPower() / shipMass;
        return thrust.multiply(multiplier);
    }

    public boolean isActive() {
        return power != 0;
    }
}
