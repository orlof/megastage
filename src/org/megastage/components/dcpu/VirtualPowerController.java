package org.megastage.components.dcpu;

import org.jdom2.Element;
import org.megastage.components.device.Device;

public class VirtualPowerController extends Device {
    public transient double supply;
    public transient double load;

    @Override
    public void config(Element elem) {
        setInfo(TYPE_POWER_CONTROLLER, 0x0100, MANUFACTORER_ENDER_INNOVATIONS);
    }

    @Override
    public void interrupt(DCPU dcpu) {
        switch(dcpu.registers[0]) {
            case 0:
                pollDevice(dcpu);
                break;
            case 1:
                getPowerFlux(dcpu);
                break;
            case 2:
                setPriority(dcpu);
                break;
            case 3:
                getPriority(dcpu);
                break;
        }
    }

    private void pollDevice(DCPU dcpu) {
        dcpu.registers[1] = (char) (supply > 0.0 ? 1: 0);
        if(supply > load) {
            dcpu.registers[2] = 0x2;
        } else if(supply < load) {
            dcpu.registers[2] = 0x1;
        } else {
            dcpu.registers[2] = 0x0;
        }
    }

    private void getPowerFlux(DCPU dcpu) {
        dcpu.registers[1] = (char) (((int) (supply + 0.5)) & 0xffff);
        dcpu.registers[2] = (char) (((int) (load + 0.5)) & 0xffff);
    }

    private void setPriority(DCPU dcpu) {
        char b = dcpu.registers[1];
        char c = dcpu.registers[2];

        Device hw = dcpu.getHardware(b);
        if(hw != null) {
            hw.priority = c;
        }
    }

    private void getPriority(DCPU dcpu) {
        char b = dcpu.registers[1];
        Device hw = dcpu.getHardware(b);
        if(hw != null) {
            dcpu.registers[1] = hw.priority;
        }
    }

}
