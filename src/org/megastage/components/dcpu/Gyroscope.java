package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import java.util.Random;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.EngineData;
import org.megastage.components.server.ShipGeometry;
import org.megastage.util.Quaternion;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Vector;

public class Gyroscope extends DCPUHardware {
    public Vector axis;

    public double maxTorque;
    public double curTorque = 0;

    public char   power = 0;

    public long   inertiaTime;
    public double inertia;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_GYRO;
        revision = 0xabc1;
        manufactorer = MANUFACTORER_PRECISION_RESEARCH;

        super.init(world, parent, element);

        axis = new Vector(
                getIntegerValue(element, "x", 0) & 0xf,
                getIntegerValue(element, "y", 0) & 0xf,
                getIntegerValue(element, "z", 1) & 0xf);
        
        maxTorque = getDoubleValue(element, "torque", 100.0);
        
        return null;
    }

    public void interrupt() {
        char a = dcpu.registers[0];

        Log.debug("a=" + Integer.toHexString(dcpu.registers[0]) + ", b=" + Integer.toHexString(dcpu.registers[1]));

        if (a == 0) {

            setTorque(dcpu.registers[1]);
        } else if (a == 1) {
            
            dcpu.registers[1] = power;
        } else if (a == 2) {
            int x = (int) Math.round(axis.x);
            int y = (int) Math.round(axis.y);
            int z = (int) Math.round(axis.z);
            char dir = (char) ((x << 8) | (y << 4) | z);
            dcpu.registers[1] = dir;
        }
    }

    public void setTorque(char torque) {
        power = torque;

        double tmp = torque < 0x8000 ? torque: -2^16 + torque;
        curTorque = maxTorque * (tmp < 0 ? tmp / 0x8000: tmp / 0x7fff);
    }
    
    public double getRotation(ShipGeometry geom) {
        if(inertiaTime < geom.updateTime) {
            //TODO this is huge perf problem, inertia change should be 
            //     calculated only for changed block
            inertiaTime = geom.updateTime;
            inertia = geom.getInertia(axis);
        }
        
        return power / inertia;
    }

}
