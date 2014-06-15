package org.megastage.components.dcpu;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class VirtualBattery extends DCPUHardware implements PowerSupply, PowerConsumer {
    
    // MW
    public int input;
    public int output;
    public int capacity;
    public double energy;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_BATTERY, 0xac1d, MANUFACTORER_URI_OASIS);

        capacity = getIntegerValue(element, "capacity", 5000);
        energy = getDoubleValue(element, "energy", 0);
        input = getIntegerValue(element, "input", 500);
        output = getIntegerValue(element, "output", 0);

        return null;
    }

    @Override
    public void interrupt(DCPU dcpu) {
        switch(dcpu.registers[0]) {
            case 0:
                getEnergy(dcpu);
                break;
            case 1:
                setInput(dcpu.registers[1]);
                break;
            case 2:
                setOutput(dcpu.registers[1]);
                break;
        }
    }

    private boolean getEnergy(DCPU dcpu) {
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
    public double consume(int ship, double available, double delta) {
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
