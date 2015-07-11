package org.megastage.components.device;

import org.jdom2.Element;
import org.megastage.components.dcpu.DCPU;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class EngineInterface extends DCPUInterface {
    @Override
    public void config(Element elem) {
        setInfo(TYPE_ENGINE, 0xad3c, MANUFACTORER_GENERAL_DRIVES);
    }

    @Override
    public void interrupt(DCPU dcpu, int eid) {
        char a = dcpu.registers[0];

        if (a == 0) {
            getDevice(eid).setPower(dcpu.registers[1]);
        } else if (a == 1) {
            dcpu.registers[1] = getDevice(eid).power;
        } else if (a == 2) {
            dcpu.registers[1] = getDevice(eid).engineId;
        }
    }

    private EngineDevice getDevice(int eid) {
        return (EngineDevice) World.INSTANCE.getComponent(eid, CompType.EngineDevice);
    }
}
