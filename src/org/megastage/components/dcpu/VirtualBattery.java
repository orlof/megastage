package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

public class VirtualBattery extends DCPUHardware implements PowerSupply, PowerConsumer {
    
    // MW
    public int input;
    public int output;
    public int capacity;
    public double energy;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_BATTERY;
        revision = 0xac1d;
        manufactorer = MANUFACTORER_URI_OASIS;

        super.init(world, parent, element);
        
        capacity = getIntegerValue(element, "capacity", 5000);
        energy = getDoubleValue(element, "energy", 0);
        input = getIntegerValue(element, "input", 500);
        output = getIntegerValue(element, "output", 0);

        return null;
    }

    @Override
    public void interrupt() {
        switch(dcpu.registers[0]) {
            case 0:
                getEnergy();
                break;
            case 1:
                setInput(dcpu.registers[1]);
                break;
            case 2:
                setOutput(dcpu.registers[1]);
                break;
        }
    }

    private boolean getEnergy() {
        dcpu.registers[1] = (char) energy;
        return true;
    }

    private boolean setInput(char val) {
        input = val;
        return true;
    }

    private boolean setOutput(char val) {
        output = val;
        return true;
    }

    @Override
    public double generatePower(double delta) {
        double outtake = delta * output;
        if(outtake > energy) {
            outtake = energy;
        }

        energy -= outtake;
        
        return outtake;
    }

    @Override
    public double consume(double available, double delta) {
        double intake = delta * input;
        if(intake > available) {
            setInput((char) 0);
            intake = 0;
        }
        
        energy += intake;
        if(energy > capacity) {
            energy = capacity;
        }

        return intake;
    }
}
