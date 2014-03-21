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
    
    public transient double minEnergy;
    public transient double maxEnergy;
    public transient double energyDensity;
    public transient double energyEvaporation;

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
        
        energyEvaporation = getDoubleValue(element, "energy_evaporation", 0.005);
        energyDensity = getDoubleValue(element, "energy_density", 50);
        
        double minRadius = getDoubleValue(element, "min_radius", 5);
        minEnergy = 4 * Math.PI * minRadius * minRadius * energyDensity;
        
        double maxRadius = getDoubleValue(element, "max_radius", 50);
        maxEnergy = 4 * Math.PI * maxRadius * maxRadius * energyDensity;

        energy = getDoubleValue(element, "energy", 0);
        power = getDoubleValue(element, "power", 1000);
         
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

    public void setEnergy(double energy) {
        if(energy < 0 ) energy = 0;
        if(energy > maxEnergy) energy = maxEnergy;

        this.energy = energy;
        
        double calculatedRadius = Math.sqrt((energy / energyDensity) / (4.0 * Math.PI));

        if(energy == 0.0) {
                status = STATUS_POWER_OFF;
        } else if(energy < minEnergy) {
                status = STATUS_FIELD_FORMING;
                calculatedRadius = 0.0;
        } else {
                status = STATUS_FIELD_ACTIVE;
        }
        
        if(calculatedRadius != radius) {
            radius = calculatedRadius;
            dirty = true;
        }
    }
    
    public void damage(Entity entity, float damage) {
        setEnergy(energy - damage);
        Log.info("Damage: " + damage + "/" + energy);
    }

    @Override
    public double consume(double available, double delta) {
        double intake = power * delta;
        if(intake > available) {
            intake = power = 0.0;
        }

        double evaporation = energy * energyEvaporation * delta;
        
        setEnergy(energy + intake - evaporation);
        
        return intake;
    }

    private void getStatus() {
        dcpu.registers[1] = status;
    }

    private void getFieldRadius() {
        dcpu.registers[1] = (char) Math.round(radius);
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
