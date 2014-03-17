package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

public class VirtualPowerPlant extends DCPUHardware implements PowerSupply, PowerConsumer {
    
    // MW
    public int production;
    public int capacity;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_POWER_PLANT;
        revision = 0x0911;
        manufactorer = MANUFACTORER_SORATOM;

        super.init(world, parent, element);
        
        capacity = getIntegerValue(element, "capacity", 10000);

        return null;
    }

    @Override
    public void interrupt() {
        switch(dcpu.registers[0]) {
            case 0:
                // GET STATUS
                getStatus();
                break;
            case 1:
                setProduction(dcpu.registers[1]);
                break;
        }
    }

    private boolean getStatus() {
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
    public double consumePower(double delta) {
        return delta * (production / 10.0);
    }

    @Override
    public void shortage() {
        setProduction(0);
    }
    
}
