package org.megastage.components.device;

import org.jdom2.Element;
import org.megastage.components.dcpu.DCPU;
import org.megastage.components.device.Device;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class InterfaceClock extends DCPUInterface {
    public int interval;
    public int intCount;
    public char ticks;
    public char interruptMessage;


    @Override
    public void config(Element elem) {
        setInfo(TYPE_CLOCK, 0x8008, MANUFACTORER_MACKAPAR);
    }

    @Override
    public void interrupt(DCPU dcpu, int eid) {
        switch(dcpu.registers[0]) {
            case 0:
                interval = dcpu.registers[1];
                break;
            case 1:
                dcpu.registers[2] = ticks;
                break;
            case 2:
                interruptMessage = dcpu.registers[1];
                break;
        }
    }

    @Override
    public void tick60hz(DCPU dcpu, int eid) {
        if (interval == 0) return;

        if (++intCount >= interval) {
            if (interruptMessage != 0) {
                dcpu.interrupt(interruptMessage);
            }
            intCount = 0;
            ticks++;
        }
    }
}
