package org.megastage.components.dcpu;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.transfer.MonitorData;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

public class VirtualMonitor extends DCPUHardware {
    public MonitorData data = new MonitorData();

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_LEM, 0x1802, MANUFACTORER_NYA_ELEKTRISKA);

        return null;
    }

    @Override
    public void interrupt(DCPU dcpu) {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        switch(a) {
            case 0:
                data.videoAddr = b;
                break;
            case 1:
                data.fontAddr = b;
                break;
            case 2:
                data.paletteAddr = b;
                break;
            case 3:
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

    @Override
    public boolean isDirty() {
        DCPU dcpu = (DCPU) World.INSTANCE.getComponent(dcpuEID, CompType.DCPU);

        dirty |= data.videoAddr == 0 ?
                data.video.update(LEMUtil.defaultVideo):
                data.video.update(dcpu.ram, data.videoAddr, 384);

        dirty |= data.fontAddr == 0 ?
                data.font.update(LEMUtil.defaultFont):
                data.font.update(dcpu.ram, data.fontAddr, 256);

        dirty |= data.paletteAddr == 0 ?
                data.palette.update(LEMUtil.defaultPalette):
                data.palette.update(dcpu.ram, data.paletteAddr, 16);
        
        return dirty;
    }

    
    @Override
    public Message synchronize(int eid) {
        return data.synchronize(eid);
    }

}
