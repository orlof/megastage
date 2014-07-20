package org.megastage.components.dcpu;

import org.megastage.util.Log;
import com.jme3.math.Vector3f;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.transfer.EngineData;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

public class VirtualEngine extends DCPUHardware implements PowerConsumer {
    public Vector3f vector;
    public float maxForce;

    public char power = 0;
    public char engineId = 0;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_ENGINE, 0xad3c, MANUFACTORER_GENERAL_DRIVES);

        vector = new Vector3f(
                getFloatValue(element, "x", 0),
                getFloatValue(element, "y", 0),
                getFloatValue(element, "z", 0)).normalizeLocal();
        
        maxForce = getFloatValue(element, "max_force", 10000.0f);
        engineId = (char) getIntegerValue(element, "engine_id", 0);
        
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
            dcpu.registers[1] = engineId;
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

    public Vector3f getForce() {
        return vector.mult(maxForce * getPowerLevel());
    }

    public boolean isActive() {
        return power != 0;
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
