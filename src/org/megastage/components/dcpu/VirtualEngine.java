package org.megastage.components.dcpu;

import com.esotericsoftware.minlog.Log;
import com.jme3.math.Vector3f;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.transfer.EngineData;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.util.Vector3d;

public class VirtualEngine extends DCPUHardware implements PowerConsumer {
    public int x, y, z;
    public float maxForce;

    public char power = 0;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_ENGINE, 0xad3c, MANUFACTORER_GENERAL_DRIVES);

        x = getIntegerValue(element, "x", 0);
        y = getIntegerValue(element, "y", 0);
        z = getIntegerValue(element, "z", -1);
        
        maxForce = getFloatValue(element, "max_power", 10000.0f);
        
        return null;
    }

    @Override
    public void interrupt(DCPU dcpu) {
        char a = dcpu.registers[0];

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
    
    public float getPowerLevel() {
        return power / 65535.0f;
    }

    public Vector3f getAcceleration(float shipMass) {
        float m = maxForce * getPowerLevel() / shipMass;
        return new Vector3f(m * x, m * y, m * z);
    }

    public boolean isActive() {
        return power != 0;
    }

    public Vector3d getForceVector() {
        double mult = maxForce * getPowerLevel();
        return new Vector3d(x * mult, y * mult, z * mult);
    }

    @Override
    public Message synchronize(int eid) {
        return EngineData.create(power).synchronize(eid);
    }
    
    @Override
    public double consume(int ship, double available, double delta) {
        double intake = delta * getPowerLevel();
        if(intake > available) {
            Log.info("Not enough power: " + intake + "/" + available);
            setPower((char) 0);
            intake = 0;
        }

        return intake;
    }
}
