package org.megastage.components.dcpu;

import com.jme3.math.Vector3f;
import org.megastage.util.Log;
import org.jdom2.Element;
import org.megastage.components.srv.VectorAttack;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class VirtualThermalLaser extends DCPUHardware {

    public enum Status {
        IDLE, FIRING, ENERGY_SHORTAGE, OVERHEAT, DAMAGED;
    }

    // static properties
    public float capCooling;
    public float maxWarming;
    public float maxDamage;
    public float maxTemperature;
    public float maxWattage;

    // weapon status
    public Status status = Status.IDLE;
    public float temperature = 0.0f;
    public char wattage = 0;
    public long beamEndTime;
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_THERMAL_LASER, 0x0010, MANUFACTORER_ENDER_INNOVATIONS);

        capCooling = getFloatValue(element, "cap_cooling", 1000.0f);
        maxWarming = getFloatValue(element, "max_warming", 10000.0f);
        maxDamage = getFloatValue(element, "max_damage", 10000.0f);
        maxTemperature = getFloatValue(element, "max_temperature", 20000.0f);
        maxWattage = getFloatValue(element, "max_wattage", 10000.0f);

        Vector3f attVec = getVector3f(element, "attack_vector", new Vector3f(0, 0, -100));
        BaseComponent[] extraComponents = new BaseComponent[] {
            new VectorAttack(attVec),
        };
        
        return extraComponents;
    }

    @Override
    public void interrupt(DCPU dcpu) {
        switch(dcpu.registers[0]) {
            case 0:
                getStatus(dcpu);
                break;
            case 1:
                setWattage(dcpu.registers[1]);
                break;
            case 2:
                fireWeapon(dcpu.registers[1]);
                break;
        }
    }

    public void getStatus(DCPU dcpu) {
        dcpu.registers[1] = (char) status.ordinal();
        dcpu.registers[2] = getChar(temperature, 0, 10000);
        dcpu.registers[3] = wattage;
    }
    
    public void setWattage(char wattage) {
        if(status != Status.FIRING && this.wattage != wattage) {
            Log.trace("wattage set %d", (int) wattage);
            this.wattage = wattage;
        }
    }
    
    public void fireWeapon(char duration) {
        if(status == Status.IDLE && wattage > 0 && duration > 0) {
            status = Status.FIRING;
            beamEndTime = World.INSTANCE.time + duration;
        }
    }

    public float getDamageRate() {
        return getFloat(wattage, 0, maxDamage);
    }

    //    @Override
//    public double consume(int ship, double available, double delta) {
//        double intake = status == STATUS_FIRING ? delta * wattage: 0.0;
//        if(intake > available) {
//            setStatusCooldown(World.INSTANCE.time);
//            intake = 0;
//        }
//
//        return intake;
//    }
}
