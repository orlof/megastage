package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.transfer.MonitorData;
import org.megastage.protocol.Network;

public class VirtualMonitor extends DCPUHardware {
    public MonitorData data = new MonitorData();

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_LEM;
        revision = 0x1802;
        manufactorer = MANUFACTORER_NYA_ELEKTRISKA;

        super.init(world, parent, element);
        
        return null;
    }
    
    @Override
    public boolean replicate() {
        return true;
    }
    
    @Override
    public boolean synchronize() {
        boolean videoChanged = data.videoAddr == 0 ?
                data.video.update(LEMUtil.defaultVideo):
                data.video.update(dcpu.ram, data.videoAddr, 384);

        boolean fontChanged = data.fontAddr == 0 ?
                data.font.update(LEMUtil.defaultFont):
                data.font.update(dcpu.ram, data.fontAddr, 256);

        boolean paletteChanged = data.paletteAddr == 0 ?
                data.palette.update(LEMUtil.defaultPalette):
                data.palette.update(dcpu.ram, data.paletteAddr, 16);
        
        return videoChanged || fontChanged || paletteChanged;
    }

    @Override
    public Network.ComponentMessage create(Entity entity) {
        Log.trace("video   [" + ((int) data.videoAddr) + "] " + data.video.toString());
        Log.trace("font    [" + ((int) data.fontAddr) + "] " + data.font.toString());
        Log.trace("palette [" + ((int) data.paletteAddr) + "] " + data.palette.toString());

        return data.create(entity);
    }

    @Override
    public void interrupt() {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        if (a == 0) {
            data.videoAddr = b;
        } else if (a == 1) {
            data.fontAddr = b;
        } else if (a == 2) {
            data.paletteAddr = b;
        } else if (a == 3) {
//            borderColor = (dcpu.registers[1] & 0xF);
        } else if (a == 4) {
            // dump font
            int offs = dcpu.registers[1];
            for (int i = 0; i < LEMUtil.defaultFont.length; i++) {
                dcpu.ram[(offs + i & 0xFFFF)] = LEMUtil.defaultFont[i];
            }
            dcpu.cycles += 256;
        } else if (a == 5) {
            // dump palette
            int offs = dcpu.registers[1];
            for (int i = 0; i < LEMUtil.defaultPalette.length; i++) {
                dcpu.ram[(offs + i & 0xFFFF)] = LEMUtil.defaultPalette[i];
            }
            dcpu.cycles += 16;
        }
    }
}
