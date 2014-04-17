package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.transfer.MonitorData;
import org.megastage.protocol.Message;
import org.megastage.protocol.Network;
import org.megastage.util.ID;

public class VirtualMonitor extends DCPUHardware {
    public MonitorData data = new MonitorData();

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        info = new DCPUHardwareInfo(TYPE_LEM, 0x1802, MANUFACTORER_NYA_ELEKTRISKA);

        super.init(world, parent, element);
        
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
    public Message replicate(Entity entity) {
        //Log.info("video   [" + ((int) data.videoAddr) + "] " + data.video.toString());
        //Log.info("font    [" + ((int) data.fontAddr) + "] " + data.font.toString());
        //Log.info("palette [" + ((int) data.paletteAddr) + "] " + data.palette.toString());
        return data.always(entity);
    }
    
    @Override
    public Message synchronize(Entity entity) {
        boolean videoChanged = data.videoAddr == 0 ?
                data.video.update(LEMUtil.defaultVideo):
                data.video.update(dcpu.ram, data.videoAddr, 384);

        boolean fontChanged = data.fontAddr == 0 ?
                data.font.update(LEMUtil.defaultFont):
                data.font.update(dcpu.ram, data.fontAddr, 256);

        boolean paletteChanged = data.paletteAddr == 0 ?
                data.palette.update(LEMUtil.defaultPalette):
                data.palette.update(dcpu.ram, data.paletteAddr, 16);
        
        return replicateIfTrue(entity, videoChanged || fontChanged || paletteChanged);
    }

}
