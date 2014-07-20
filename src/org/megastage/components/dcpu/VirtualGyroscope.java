package org.megastage.components.dcpu;

import org.megastage.util.Log;
import com.jme3.math.Vector3f;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.components.Explosion;
import org.megastage.components.transfer.GyroscopeData;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

public class VirtualGyroscope extends DCPUHardware implements PowerConsumer {
    public static transient final char STATUS_OFF = 0;
    public static transient final char STATUS_ON = 1;
    public static transient final char STATUS_NO_POWER = 2;

    public Vector3f axis;
    public float maxTorque;
    public char torque = 0;
    public char gyroscopeId;
    
    public int mapVersion;
    public float inertia;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_GYRO, 0xabcd, MANUFACTORER_GENERAL_DRIVES);

        axis = new Vector3f(
                getFloatValue(element, "x", 0.0f),
                getFloatValue(element, "y", 0.0f),
                getFloatValue(element, "z", 1.0f));
        
        maxTorque = getFloatValue(element, "max_torque", 100.0f);
        gyroscopeId = (char) getIntegerValue(element, "gyroscope_id", 0);
        
        return null;
    }

    @Override
    public void interrupt(DCPU dcpu) {
        char a = dcpu.registers[0];

        if (a == 0) {
            setTorque(dcpu.registers[1]);
        } else if (a == 1) {
            if(torque == 0) {
                dcpu.registers[1] = STATUS_OFF;
            } else {
                dcpu.registers[1] = STATUS_ON;
            }
            dcpu.registers[2] = torque;
        } else if (a == 2) {
            dcpu.registers[1] = gyroscopeId;
        }
    }

    public void setTorque(char torque) {
        if(torque == 0x8000) {
            World.INSTANCE.setComponent(shipEID, CompType.Explosion, new Explosion());
            return;
        } 

        if(this.torque != torque) {
            this.torque = torque;
            dirty = true;
        }
    }
    
    private float getTorquePower() {
        return maxTorque * getSignedTorque() / 0x7fff;
    }
    
    private int getSignedTorque() {
        return torque < 0x8000 ? torque: torque - 65536;
    }
    
    public float getRotation(ShipGeometry geom) {
        if(mapVersion < geom.map.version) {
            //TODO this is huge perf problem, inertia change should be 
            //     calculated only for changed block
            mapVersion = geom.map.version;
            inertia = geom.getInertia(axis);
        }
        
        return getTorquePower() / inertia;
    }
    
    @Override
    public Message synchronize(int eid) {
        return GyroscopeData.create(torque).synchronize(eid);
    }
    
    @Override
    public double consume(int ship, double available, double delta) {
        double intake = delta * getPowerLevel();
        if(intake > available) {
            Log.info("Not enough power: " + intake + "/" + available);
            setTorque((char) 0);
            intake = 0;
        }

        return intake;
    }

    public double getPowerLevel() {
        return Math.abs(getSignedTorque() / 200.0f);
    }
}
