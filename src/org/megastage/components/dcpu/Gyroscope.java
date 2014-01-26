package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.util.Vector;

public class Gyroscope extends DCPUHardware {
    public static final char STATUS_OFF = 0;
    public static final char STATUS_ON = 1;
    public static final char STATUS_NO_POWER = 2;

    public Vector axis;

    public double maxTorque;
    public double curTorque = 0;

    public char   power = 0;

    public long   inertiaTime;
    public double inertia;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_GYRO;
        revision = 0xabcd;
        manufactorer = MANUFACTORER_GENERAL_DRIVES;

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

    public void setTorque(char torque) {
        Log.debug("" + (int) torque);
        power = torque;

        double tmp = torque < 0x8000 ? torque: torque - (2<<15);
        curTorque = maxTorque * (tmp < 0 ? tmp / 0x8000: tmp / 0x7fff);
    }
    
    public double getRotation(ShipGeometry geom) {
        if(inertiaTime < geom.updateTime) {
            //TODO this is huge perf problem, inertia change should be 
            //     calculated only for changed block
            inertiaTime = geom.updateTime;
            inertia = geom.getInertia(axis);
            Log.info("Inertia: " + inertia);
        }
        
        return curTorque / inertia;
    }
    
    public static void main(String[] args) throws Exception {
        Gyroscope gyro = new Gyroscope();
        gyro.maxTorque = 100;
        
        gyro.setTorque((char) 0x0000);
        System.out.println((int) gyro.power);
        System.out.println(gyro.curTorque);
    
        gyro.setTorque((char) 0x7fff);
        System.out.println((int) gyro.power);
        System.out.println(gyro.curTorque);
        
        gyro.setTorque((char) 0x8000);
        System.out.println((int) gyro.power);
        System.out.println(gyro.curTorque);
    }

}
