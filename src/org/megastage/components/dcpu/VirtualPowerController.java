package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

public class VirtualPowerController extends DCPUHardware {
    public transient double supply;
    public transient double load;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_POWER_CONTROLLER;
        revision = 0x0100;
        manufactorer = MANUFACTORER_ENDER_INNOVATIONS;

        super.init(world, parent, element);
        
        return null;
    }

    public void interrupt() {
        switch(dcpu.registers[0]) {
            case 0:
                pollDevice();
                break;
            case 1:
                getPowerFlux();
                break;
            case 2:
                setPriority(dcpu.registers[1], dcpu.registers[2]);
                break;
            case 3:
                getPriority(dcpu.registers[1]);
                break;
        }
    }

    private void pollDevice() {
        dcpu.registers[1] = (char) (supply > 0.0 ? 1: 0);
        if(supply > load) {
            dcpu.registers[2] = 0x2;
        } else if(supply < load) {
            dcpu.registers[2] = 0x1;
        } else {
            dcpu.registers[2] = 0x0;
        }
    }

    private void getPowerFlux() {
        dcpu.registers[1] = (char) (((int) (supply + 0.5)) & 0xffff);
        dcpu.registers[2] = (char) (((int) (load + 0.5)) & 0xffff);
    }

    private void setPriority(char b, char c) {
        if (b < dcpu.hardware.size()) {
            DCPUHardware hw = dcpu.hardware.get(b);
            hw.priority = c;
        }
    }

    private void getPriority(char b) {
        if (b < dcpu.hardware.size()) {
            DCPUHardware hw = dcpu.hardware.get(b);
            dcpu.registers[1] = (char) (hw.priority & 0xffff);
        }
    }

}
