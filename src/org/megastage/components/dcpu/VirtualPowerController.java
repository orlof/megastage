package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.systems.srv.PowerControllerSystem;

public class VirtualPowerController extends DCPUHardware {

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void getPowerFlux() {
        throw new UnsupportedOperationException("Not supported yet.");
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
