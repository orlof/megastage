package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

public class VirtualPowerPlant extends DCPUHardware {
    
    public int production;
    public int capacity;
    public double flux;
    
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_POWER_PLANT;
        revision = 0x0911;
        manufactorer = MANUFACTORER_SORATOM;

        super.init(world, parent, element);
        
        capacity = getIntegerValue(element, "capacity", 10000);

        return null;
    }

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

    public void resetFlux(float delta) {
        flux = delta * production;
    }
    
}
