package org.megastage.components.dcpu;

import org.megastage.components.transfer.ThermalLaserData;
import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.protocol.Message;
import org.megastage.util.Time;

public class VirtualThermalLaser extends DCPUHardware {
    public static transient final char STATUS_DORMANT = 0;
    public static transient final char STATUS_FIRING = 1;
    public static transient final char STATUS_COOLDOWN = 2;

    public static transient final char ERROR_NOMINAL = 0;
    public static transient final char ERROR_NOT_ENOUGH_ENERGY = 1;
    public static transient final char ERROR_OVERHEATED = 2;
    public static transient final char ERROR_SERIOUSLY_BROKEN = 3;

    public transient float maxRange;
    public transient long startTime;
    public transient long duration;

    public char status = STATUS_DORMANT;
    public char wattage = 1;
    public float range;
    
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_THERMAL_LASER;
        revision = 0x0010;
        manufactorer = MANUFACTORER_ENDER_INNOVATIONS;

        super.init(world, parent, element);
        
        range = maxRange = getFloatValue(element, "max_range", 100);

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
            this.wattage = wattage;
        }
    }

    public void fireWeapon(char duration) {
        if(status == STATUS_DORMANT && wattage > 0 && dcpu.registers[1] <= 300) {
            dirty = true;
            status = STATUS_FIRING;
            startTime = Time.value;
            this.duration = 100 * duration;
        }
    }

    @Override
    public Message replicate(Entity entity) {
        dirty = false;
        return ThermalLaserData.create(status, wattage, range).always(entity);
    }
    
    @Override
    public Message synchronize(Entity entity) {
        return replicateIfDirty(entity);
    }

    public void setRange(float range) {
        if(this.range != range) {
            this.range = range;
            this.dirty = true;
        }
    }
}
