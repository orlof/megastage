package org.megastage.components.dcpu;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class VirtualPowerPlant extends DCPUHardware implements PowerSupply, PowerConsumer {
    
    // MW
    public int production;
    public int capacity;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_POWER_PLANT, 0x0911, MANUFACTORER_SORATOM);
        
        capacity = getIntegerValue(element, "capacity", 5000);
        production = capacity;

        return null;
    }

    @Override
    public void interrupt(DCPU dcpu) {
        switch(dcpu.registers[0]) {
            case 0:
                // GET STATUS
                getStatus(dcpu);
                break;
            case 1:
                setProduction(dcpu.registers[1]);
                break;
        }
    }

    private boolean getStatus(DCPU dcpu) {
        dcpu.registers[1] = 0;
        return true;
    }

    private boolean setProduction(int prod) {
        if(prod <= capacity) {
            this.production = prod;
        }
        return true;
    }

    @Override
    public double generatePower(double delta) {
        return delta * production;
    }

    @Override
    public double consume(int ship, double available, double delta) {
        double intake = delta * (production / 10.0);
        if(intake > available) {
            setProduction(0);
            intake = 0;
        }

        return intake;
    }

}
