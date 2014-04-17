package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

public class VirtualClock extends DCPUHardware {
    public int interval;
    public int intCount;
    public char ticks;
    public char interruptMessage;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_CLOCK;
        revision = 0x8008;
        manufactorer = MANUFACTORER_MACKAPAR;

        super.init(world, parent, element);
        
        return null;
    }

    public void interrupt(DCPU dcpu) {
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
