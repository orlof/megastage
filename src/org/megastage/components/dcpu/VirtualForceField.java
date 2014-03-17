package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.protocol.Message;
import org.megastage.util.Mapper;

public class VirtualForceField extends DCPUHardware implements PowerConsumer {
    public static transient final char STATUS_POWER_OFF = 0;
    public static transient final char STATUS_FIELD_FORMING = 1;
    public static transient final char STATUS_FIELD_ACTIVE = 2;
    
    public transient double energy;
    public transient double power;
    public transient double radius;
    public transient char status;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_FORCE_FIELD;
        revision = 0x0944;
        manufactorer = MANUFACTORER_CRADLE_TECH;

        super.init(world, parent, element);
        
        //radius = getFloatValue(element, "radius", 20);
        //maxEnergy = getFloatValue(element, "max_energy", 40000);
        energy = getFloatValue(element, "energy", 0);
         
        return null;
    }

    @Override
    public void interrupt() {
        switch(dcpu.registers[0]) {
            case 0:
                getStatus();
                break;
            case 1:
                getFieldRadius();
                break;
            case 2:
                getFieldEnergy();
                break;
            case 3:
                setEnergyIntake(dcpu.registers[1]);
                break;
        }
    }

    @Override
    public Message replicate(Entity entity) {
        dirty = false;
        return ForceFieldData.create((float) radius, status).always(entity);
    }
    
    @Override
    public Message synchronize(Entity entity) {
        return replicateIfDirty(entity);
    }

    public void setEnergy(Entity entity, double energy) {
        if(energy < 0.0) energy = 0.0;

        if(this.energy == energy) return;
        
        this.energy = energy;
        this.dirty = true;

        double r = getRadius();
        if(r != radius) {
            this.radius = r;
            Mapper.COLLISION_SPHERE.get(entity).radius = r;
 
            if(radius == 0.0) {
                status = STATUS_POWER_OFF;
            } else if(radius < 5.0) {
                status = STATUS_FIELD_FORMING;
            } else {
                status = STATUS_FIELD_ACTIVE;
            }
        }
    }
    
    public double getRadius() {
        double r = Math.sqrt((energy / 50.0) / (4 * Math.PI));
        return r < 5.0 ? 0.0: r;
    }

    public void damage(Entity entity, float damage) {
        setEnergy(entity, energy - damage);
        Log.info("Energy : " + energy);
    }

    @Override
    public double consumePower(double delta) {
        return power * delta;
    }

    @Override
    public void shortage() {
        power = 0.0;
    }

    private void getStatus() {
        dcpu.registers[1] = status;
    }

    private void getFieldRadius() {
        if(radius < 5.0) {
            dcpu.registers[1] = 0;
        } else {
            dcpu.registers[1] = (char) Math.round(radius);
        }
    }

    private void getFieldEnergy() {
        final long e = Math.round(energy);
        dcpu.registers[1] = (char) (e >> 16 & 0xffff);
        dcpu.registers[2] = (char) (e & 0xffff);
    }

    private void setEnergyIntake(char power) {
        this.power = power;
    }
}
