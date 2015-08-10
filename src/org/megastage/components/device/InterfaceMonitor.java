package org.megastage.components.device;

import org.jdom2.Element;
import org.megastage.components.dcpu.DCPU;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class InterfaceMonitor extends DCPUInterface {
    @Override
    public void config(Element elem) {
        setInfo(TYPE_LEM, 0x1802, MANUFACTORER_NYA_ELEKTRISKA);
    }

    @Override
    public void interrupt(DCPU dcpu, int eid) {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        switch(a) {
            case 0:
                getDevice(eid).setVideoAddr(b);
                break;
            case 1:
                getDevice(eid).setFontAddr(b);
                break;
            case 2:
                getDevice(eid).setPaletteAddr(b);
                break;
            case 3:
                // Not supported
                // borderColor = (dcpu.registers[1] & 0xF);
                break;
            case 4:
                // dump font
                int offs = dcpu.registers[1];
                for (int i = 0; i < LEMUtil.defaultFont.length; i++) {
                    dcpu.ram[(offs + i & 0xFFFF)] = LEMUtil.defaultFont[i];
                }
                dcpu.cycles += 256;
                break;
            case 5:
                // dump palette
                offs = dcpu.registers[1];
                for (int i = 0; i < LEMUtil.defaultPalette.length; i++) {
                    dcpu.ram[(offs + i & 0xFFFF)] = LEMUtil.defaultPalette[i];
                }
                dcpu.cycles += 16;
                break;
        }
    }

    private DeviceMonitor getDevice(int eid) {
        return (DeviceMonitor) World.INSTANCE.getComponent(eid, CompType.DeviceMonitor);
    }
}
