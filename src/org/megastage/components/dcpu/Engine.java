package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import java.util.Random;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.transfer.EngineData;
import org.megastage.protocol.Network;
import org.megastage.util.Time;
import org.megastage.util.Vector3d;

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
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_ENGINE;
        revision = 0xad3c;
        manufactorer = MANUFACTORER_GENERAL_DRIVES;

        super.init(world, parent, element);

        x = getIntegerValue(element, "x", 0);
        y = getIntegerValue(element, "y", 0);
        z = getIntegerValue(element, "z", -1);
        
        maxForce = getDoubleValue(element, "max_power", 10000.0);
        
        return null;
    }

    public void interrupt() {
        char a = dcpu.registers[0];

        Log.trace("a=" + Integer.toHexString(dcpu.registers[0]) + ", b=" + Integer.toHexString(dcpu.registers[1]));

        if (a == 0) {
            setPowerTarget((char) (dcpu.registers[1]));
        } else if (a == 1) {
            
            dcpu.registers[2] = powerActual;
            dcpu.registers[1] = status;
        } else if (a == 2) {
            
            char dir = (char) (((x & 0xf) << 8) | ((y & 0xf) << 4) | (z & 0xf));
            dcpu.registers[1] = dir;
        }
    }

    public void setPowerTarget(char power) {
        powerTarget = power;
        if(powerTarget == 0) {
            ignitionCompleted = 0;
        } else {
            if(status == STATUS_OFF) {
                status = STATUS_WARMUP;
                ignitionCompleted = getIgnitionTime();
            }
        }
    }
    
    public double getPowerLevel() {
        if(powerTarget != powerActual && Time.value >= ignitionCompleted) {
            powerActual = powerTarget;
            status = powerTarget == 0 ? STATUS_OFF: STATUS_ON;
            dirty = true;
        }
        return powerActual / 65535.0d;
    }

    public Vector3d getAcceleration(double shipMass) {
        double m = maxForce * getPowerLevel() / shipMass;
        return new Vector3d(m*x, m*y, m*z);
    }

    public boolean isActive() {
        return status != STATUS_OFF;
    }

    public Vector3d getForceVector() {
        double mult = maxForce * getPowerLevel();
        return new Vector3d(x * mult, y * mult, z * mult);
    }

    private long getIgnitionTime() {
        return 0;
        // return random.nextInt(40000) + 20000 + Time.value;
    }

    private static Random random = new Random();

    public EngineData data = new EngineData();

    @Override
    public Network.ComponentMessage create(Entity entity) {
        dirty = false;

        data.power = powerActual;
        return data.create(entity);
    }

    @Override
    public boolean replicate() {
        return true;
    }
    
    private boolean dirty = false;

    @Override
    public boolean synchronize() {
        return dirty;
    }
    
    
}
