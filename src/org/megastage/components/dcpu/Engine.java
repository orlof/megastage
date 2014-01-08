package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import java.util.Random;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.EngineData;
import org.megastage.components.MonitorData;
import org.megastage.protocol.Network;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Vector;

public class Engine extends DCPUHardware {
    public static final char STATUS_OFF = 0;
    public static final char STATUS_WARMUP = 1;
    public static final char STATUS_ON = 2;
    
    public int x, y, z;
    public char status = STATUS_OFF;

    public double maxForce;

    public char powerActual = 0;
    public char powerTarget = 0;

    private long ignitionCompleted = 0;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_ENGINE;
        revision = 0xad3c;
        manufactorer = MANUFACTORER_GENERAL_DRIVES;

        super.init(world, parent, element);

        x = getIntegerValue(element, "x", 0) & 0xf;
        y = getIntegerValue(element, "y", 0) & 0xf;
        z = getIntegerValue(element, "z", -1) & 0xf;
        
        maxForce = getDoubleValue(element, "max_power", 10000.0);
    }

    public void interrupt() {
        char a = dcpu.registers[0];

        Log.debug("a=" + Integer.toHexString(dcpu.registers[0]) + ", b=" + Integer.toHexString(dcpu.registers[1]));

        if (a == 0) {
            powerTarget = dcpu.registers[1];
            if(powerTarget == 0) {
                ignitionCompleted = 0;
            } else {
                if(status == STATUS_OFF) {
                    status = STATUS_WARMUP;
                    ignitionCompleted = getIgnitionTime();
                }
            }
        } else if (a == 1) {
            dcpu.registers[2] = powerActual;
            dcpu.registers[1] = status;

        } else if (a == 2) {
            char dir = (char) ((x << 8) | (y << 4) | z);
            dcpu.registers[1] = dir;
        }
    }

    public double getPowerLevel() {
        if(powerTarget != powerActual && ServerGlobals.time >= ignitionCompleted) {
            powerActual = powerTarget;
            status = powerTarget == 0 ? STATUS_OFF: STATUS_ON;
            dirty = true;
        }
        return powerActual / 65535.0d;
    }

    public Vector getAcceleration(double shipMass) {
        double mult = -1.0 * maxForce * getPowerLevel() / shipMass;
        return new Vector(x * mult, y * mult, z * mult);
    }

    public boolean isActive() {
        return status == STATUS_ON;
    }

    public Vector getForceVector() {
        double mult = maxForce * getPowerLevel();
        return new Vector(x * mult, y * mult, z * mult);
    }

    private long getIgnitionTime() {
        return random.nextInt(40000) + 20000 + ServerGlobals.time;
    }

    private static Random random = new Random();

    public EngineData data = new EngineData();

    @Override
    public Object create(Entity entity) {
        dirty = false;

        data.power = powerActual;
        return data.create(entity);
    }

    private boolean dirty = false;

    @Override
    public boolean isUpdated() {
        return dirty;
    }
    
    
}
