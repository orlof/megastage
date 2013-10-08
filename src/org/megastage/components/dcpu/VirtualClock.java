package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

public class VirtualClock extends DCPUHardware {
    private int interval;
    private int intCount;
    private char ticks;
    private char interruptMessage;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_CLOCK;
        revision = 0x8008;
        manufactorer = MANUFACTORER_MACKAPAR;

        super.init(world, parent, element);
    }

    public void interrupt() {
        int a = dcpu.registers[0];
        if (a == 0)
            interval = dcpu.registers[1];
        else if (a == 1)
            dcpu.registers[2] = ticks;
        else if (a == 2)
            interruptMessage = dcpu.registers[1];
    }

    public void tick60hz() {
        if (interval == 0) return;
        if (++intCount >= interval) {
            if (interruptMessage != 0) dcpu.interrupt(interruptMessage);
            intCount = 0;
            ticks++;
        }
    }
}