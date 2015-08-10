package org.megastage.components.device;

import org.megastage.components.client.ClientVideoMemory;
import org.megastage.components.dcpu.DCPU;
import org.megastage.components.dcpu.DCPUConnection;
import org.megastage.components.generic.EntityReference;
import org.megastage.components.transfer.MonitorData;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.protocol.Carrier;
import org.megastage.protocol.ComponentMessage;
import org.megastage.protocol.Message;
import org.megastage.util.RAM;

public class DeviceMonitor extends BaseComponent implements Carrier {
    public char videoAddr = 0x8000;
    public RAM video = new RAM(LEMUtil.defaultVideo);

    public char fontAddr = 0x0000;
    public RAM font = new RAM(LEMUtil.defaultFont);

    public char paletteAddr = 0x0000;
    public RAM palette = new RAM(LEMUtil.defaultPalette);

    @Override
    public Message replicate(int eid) {
        DCPUConnection ref = (DCPUConnection) World.INSTANCE.getComponent(eid, CompType.DCPUConnection);
        DCPU dcpu = (DCPU) World.INSTANCE.getComponent(ref.eid, CompType.DCPU);

        if(dcpu == null) {
            // only happens while waiting CleanupSystem
            return null;
        }

        boolean dirty = videoAddr == 0 ?
                video.update(LEMUtil.defaultVideo):
                video.update(dcpu.ram, videoAddr, 384);

        dirty |= fontAddr == 0 ?
                font.update(LEMUtil.defaultFont):
                font.update(dcpu.ram, fontAddr, 256);

        dirty |= paletteAddr == 0 ?
                palette.update(LEMUtil.defaultPalette):
                palette.update(dcpu.ram, paletteAddr, 16);
        
        if(dirty) {
            return new ComponentMessage(eid, this);
        }

        return null;
    }

    @Override
    public void receive(int eid) {
        ClientVideoMemory videoMemory = World.INSTANCE.getOrCreateComponent(eid, CompType.ClientVideoMemory, ClientVideoMemory.class);
        videoMemory.update(this);
    }

    public void setVideoAddr(char addr) {
        videoAddr = addr;
    }

    public void setFontAddr(char addr) {
        fontAddr = addr;
    }

    public void setPaletteAddr(char addr) {
        paletteAddr = addr;
    }
}
