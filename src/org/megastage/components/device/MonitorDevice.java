package org.megastage.components.device;

import org.megastage.components.dcpu.DCPU;
import org.megastage.components.generic.EntityReference;
import org.megastage.components.transfer.MonitorData;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

public class MonitorDevice extends Device {
    public MonitorData data = new MonitorData();

    @Override
    public boolean isDirty(int eid) {
        EntityReference ref = (EntityReference) World.INSTANCE.getComponent(eid, CompType.ConnectedTo);
        DCPU dcpu = (DCPU) World.INSTANCE.getComponent(ref.eid, CompType.DCPU);

        if(dcpu == null) {
            // only happens while waiting CleanupSystem
            return false;
        }

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

    public void setVideoAddr(char addr) {
        data.videoAddr = addr;
    }

    public void setFontAddr(char addr) {
        data.fontAddr = addr;
    }

    public void setPaletteAddr(char addr) {
        data.paletteAddr = addr;
    }
}
