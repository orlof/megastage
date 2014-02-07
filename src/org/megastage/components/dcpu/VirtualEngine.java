package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.transfer.EngineData;
import org.megastage.protocol.Network;
import org.megastage.util.Vector3d;

public class VirtualEngine extends DCPUHardware {
    public int x, y, z;
    public double maxForce;

    public char power = 0;

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

    @Override
    public void interrupt() {
        char a = dcpu.registers[0];

        Log.trace("a=" + Integer.toHexString(dcpu.registers[0]) + ", b=" + Integer.toHexString(dcpu.registers[1]));

        if (a == 0) {
            setPower(dcpu.registers[1]);
        } else if (a == 1) {
            
            dcpu.registers[1] = power;
        } else if (a == 2) {
            
            char dir = (char) (((x & 0xf) << 8) | ((y & 0xf) << 4) | (z & 0xf));
            dcpu.registers[1] = dir;
        }
    }

    public void setPower(char power) {
        if(this.power != power) {
            this.power = power;
            this.dirty = true;
        }
    }
    
    public double getPowerLevel() {
        return power / 65535.0d;
    }

    public Vector3d getAcceleration(double shipMass) {
        double m = maxForce * getPowerLevel() / shipMass;
        return new Vector3d(m * x, m * y, m * z);
    }

    public boolean isActive() {
        return power != 0;
    }

    public Vector3d getForceVector() {
        double mult = maxForce * getPowerLevel();
        return new Vector3d(x * mult, y * mult, z * mult);
    }

    private boolean dirty = false;

    @Override
    public Network.ComponentMessage create(Entity entity) {
        dirty = false;

        EngineData data = new EngineData();
        data.power = power;

        return data.create(entity);
    }

    @Override
    public boolean replicate() {
        return true;
    }
    
    @Override
    public boolean synchronize() {
        return dirty;
    }
}
