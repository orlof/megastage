package org.megastage.components.dcpu;

import org.megastage.util.Log;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.components.Explosion;
import org.megastage.components.transfer.GyroscopeData;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.util.Vector3d;

public class VirtualGyroscope extends DCPUHardware implements PowerConsumer {
    public static transient final char STATUS_OFF = 0;
    public static transient final char STATUS_ON = 1;
    public static transient final char STATUS_NO_POWER = 2;

    public Vector3f axis;

    public float maxTorque;
    public float curTorque = 0;

    public char   power = 0;

    public int    mapVersion;
    public float inertia;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_GYRO, 0xabcd, MANUFACTORER_GENERAL_DRIVES);

        axis = new Vector3f(
                getIntegerValue(element, "x", 0) & 0xf,
                getIntegerValue(element, "y", 0) & 0xf,
                getIntegerValue(element, "z", 1) & 0xf);
        
        maxTorque = getFloatValue(element, "torque", 100.0f);
        
        return null;
    }

    @Override
    public void interrupt(DCPU dcpu) {
        char a = dcpu.registers[0];

        if (a == 0) {

            setTorque(shipEID, dcpu.registers[1]);
        } else if (a == 1) {
            if(power == 0) {
                dcpu.registers[1] = STATUS_OFF;
            } else {
                dcpu.registers[1] = STATUS_ON;
            }
            
            dcpu.registers[2] = power;
        } else if (a == 2) {
            int x = (int) Math.round(axis.x);
            int y = (int) Math.round(axis.y);
            int z = (int) Math.round(axis.z);
            char dir = (char) (((x & 0xf) << 8) | ((y & 0xf) << 4) | (z & 0xf));
            dcpu.registers[1] = dir;
        }
    }

    public void setTorque(int ship, char torque) {
        if(torque == 0x8000) {
            World.INSTANCE.setComponent(ship, CompType.Explosion, new Explosion());
            return;
        } 

        if(power != torque) {
            power = torque;

            float tmp = torque < 0x8000 ? torque: torque - 65536;
            curTorque = maxTorque * tmp / 0x7fff;
            dirty = true;
        }
    }
    
    public double getPowerLevel() {
        return Math.abs(curTorque / 200.0);
    }
    
    public float getRotation(ShipGeometry geom) {
        if(mapVersion < geom.map.version) {
            //TODO this is huge perf problem, inertia change should be 
            //     calculated only for changed block
            mapVersion = geom.map.version;
            inertia = geom.getInertia(axis);
        }
        
        return curTorque / inertia;
    }
    
    @Override
    public Message synchronize(int eid) {
        return GyroscopeData.create(power).synchronize(eid);
    }
    
    @Override
    public double consume(int ship, double available, double delta) {
        double intake = delta * getPowerLevel();
        if(intake > available) {
            Log.info("Not enough power: " + intake + "/" + available);
            setTorque(ship, (char) 0);
            intake = 0;
        }

        return intake;
    }
}
