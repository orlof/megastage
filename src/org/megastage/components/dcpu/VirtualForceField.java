package org.megastage.components.dcpu;

import org.megastage.util.Log;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

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
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_FORCE_FIELD, 0x0944, MANUFACTORER_CRADLE_TECH);

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
    public void interrupt(DCPU dcpu) {
        switch(dcpu.registers[0]) {
            case 0:
                getStatus(dcpu);
                break;
            case 1:
                getFieldRadius(dcpu);
                break;
            case 2:
                getFieldEnergy(dcpu);
                break;
            case 3:
                setEnergyIntake(dcpu.registers[1]);
                break;
        }
    }

    @Override
    public Message synchronize(int eid) {
        return ForceFieldData.create((float) radius, status).synchronize(eid);
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
    
    public void damage(int eid, float damage) {
        setEnergy(energy - damage);
        Log.info("Damage: " + damage + "/" + energy);
    }

    @Override
    public double consume(int ship, double available, double delta) {
        double intake = power * delta;
        if(intake > available) {
            intake = power = 0.0;
        }
        
        double evaporation = energy * energyEvaporation * delta;
        
        setEnergy(energy + intake - evaporation);
        
        return intake;
    }

    private void getStatus(DCPU dcpu) {
        dcpu.registers[1] = status;
    }

    private void getFieldRadius(DCPU dcpu) {
        dcpu.registers[1] = (char) Math.round(radius);
    }

    private void getFieldEnergy(DCPU dcpu) {
        final long e = Math.round(energy);
        dcpu.registers[1] = (char) (e >> 16 & 0xffff);
        dcpu.registers[2] = (char) (e & 0xffff);
    }

    private void setEnergyIntake(char power) {
        this.power = power;
    }
}
