package org.megastage.components.dcpu;

import org.megastage.components.transfer.ThermalLaserData;
import org.megastage.util.Log;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

public class VirtualThermalLaser extends DCPUHardware implements PowerConsumer {
    public static transient final char STATUS_DORMANT = 0;
    public static transient final char STATUS_FIRING = 1;
    public static transient final char STATUS_COOLDOWN = 2;

    public static transient final char ERROR_NOMINAL = 0;
    public static transient final char ERROR_NOT_ENOUGH_ENERGY = 1;
    public static transient final char ERROR_OVERHEATED = 2;
    public static transient final char ERROR_SERIOUSLY_BROKEN = 3;

    public transient long startTime;
    public transient long duration;

    public char status = STATUS_DORMANT;
    public char wattage = 0;
    public float range;
    public int cooldownSpeed;
    private float distance;
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_THERMAL_LASER, 0x0010, MANUFACTORER_ENDER_INNOVATIONS);

        range = getFloatValue(element, "range", 100);
        cooldownSpeed = getIntegerValue(element, "cooldown_speed", 20);

        return null;
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
        dcpu.registers[1] = status;
        dcpu.registers[2] = ERROR_NOMINAL;
    }
    
    public void setWattage(char wattage) {
        if(status != STATUS_FIRING && wattage <= 5000 && this.wattage != wattage) {
            Log.trace("" + (int) wattage);
            this.wattage = wattage;
        }
    }
    
    public void setStatusCooldown(long time) {
        startTime = time;
        duration = duration * wattage / cooldownSpeed;
        status = VirtualThermalLaser.STATUS_COOLDOWN;
        dirty = true;
    }

    public void fireWeapon(char duration) {
        if(status == STATUS_DORMANT && wattage > 0 && duration <= 300) {
            dirty = true;
            status = STATUS_FIRING;
            startTime = World.INSTANCE.time;
            this.duration = 100 * duration;
        }
    }

    @Override
    public Message synchronize(int eid) {
        return ThermalLaserData.create(status, wattage, distance).synchronize(eid);
    }
    
    public void setHit(float distance) {
        if(distance == 0f) {
            distance = range;
        }
        
        if(this.distance != distance) {
            this.distance = distance;
            this.dirty = true;
        }
    }

    @Override
    public double consume(int ship, double available, double delta) {
        double intake = status == STATUS_FIRING ? delta * wattage: 0.0;
        if(intake > available) {
            setStatusCooldown(World.INSTANCE.time);
            intake = 0;
        }

        return intake;
    }
}
