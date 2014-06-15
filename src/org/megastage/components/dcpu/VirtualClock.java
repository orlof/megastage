package org.megastage.components.dcpu;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class VirtualClock extends DCPUHardware {
    public int interval;
    public int intCount;
    public char ticks;
    public char interruptMessage;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_CLOCK, 0x8008, MANUFACTORER_MACKAPAR);
        return null;
    }

    public void interrupt(int ship, DCPU dcpu) {
        int a = dcpu.registers[0];

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
    public void tick60hz(DCPU dcpu) {
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
