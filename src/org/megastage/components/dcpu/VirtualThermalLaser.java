package org.megastage.components.dcpu;

import org.megastage.components.transfer.ThermalLaserData;
import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.protocol.Message;
import org.megastage.util.GlobalTime;
import org.megastage.util.Vector3d;

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
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_THERMAL_LASER;
        revision = 0x0010;
        manufactorer = MANUFACTORER_ENDER_INNOVATIONS;

        super.init(world, parent, element);
        
        range = getFloatValue(element, "range", 100);
        cooldownSpeed = getIntegerValue(element, "cooldown_speed", 20);

        return null;
    }

    @Override
    public void interrupt() {
        switch(dcpu.registers[0]) {
            case 0:
                getStatus();
                break;
            case 1:
                setWattage(dcpu.registers[1]);
                break;
            case 2:
                fireWeapon(dcpu.registers[1]);
                break;
        }
    }

    public void getStatus() {
        dcpu.registers[1] = status;
        dcpu.registers[2] = ERROR_NOMINAL;
    }
    
    public void setWattage(char wattage) {
        if(status != STATUS_FIRING && wattage <= 5000 && this.wattage != wattage) {
            Log.info("" + (int) wattage);
            this.wattage = wattage;
        }
    }
    
    public void setStatusCooldown() {
        startTime = GlobalTime.value;
        duration = duration * wattage / cooldownSpeed;
        status = VirtualThermalLaser.STATUS_COOLDOWN;
        dirty = true;
    }

    public void fireWeapon(char duration) {
        if(status == STATUS_DORMANT && wattage > 0 && duration <= 300) {
            dirty = true;
            status = STATUS_FIRING;
            startTime = GlobalTime.value;
            this.duration = 100 * duration;
        }
    }

    @Override
    public Message replicate(Entity entity) {
        dirty = false;
        return ThermalLaserData.create(status, wattage, distance).always(entity);
    }
    
    @Override
    public Message synchronize(Entity entity) {
        return replicateIfDirty(entity);
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
    public double consume(double available, double delta) {
        double intake = status == STATUS_FIRING ? delta * wattage: 0.0;
        if(intake > available) {
            setStatusCooldown();
            intake = 0;
        }

        return intake;
    }

}
