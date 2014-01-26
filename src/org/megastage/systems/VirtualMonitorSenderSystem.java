package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.transfer.MonitorData;
import org.megastage.components.dcpu.LEMUtil;
import org.megastage.components.dcpu.VirtualMonitor;

public class VirtualMonitorSenderSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<VirtualMonitor> virtualMonitorMapper;

    public VirtualMonitorSenderSystem() {
        super(Aspect.getAspectForAll(VirtualMonitor.class));
    }

    @Override
    protected void process(Entity entity) {
//        VirtualMonitor mon = virtualMonitorMapper.get(entity);
//        MonitorData data = mon.data;
//
//        boolean videoChanged = data.videoAddr == 0 ?
//                data.video.update(LEMUtil.defaultVideo):
//                data.video.update(mon.dcpu.ram, data.videoAddr, 384);
//
//        boolean fontChanged = data.fontAddr == 0 ?
//                data.font.update(LEMUtil.defaultFont):
//                data.font.update(mon.dcpu.ram, data.fontAddr, 256);
//
//        boolean paletteChanged = data.paletteAddr == 0 ?
//                data.palette.update(LEMUtil.defaultPalette):
//                data.palette.update(mon.dcpu.ram, data.paletteAddr, 16);
//
//        if(videoChanged || fontChanged || paletteChanged) {
//            Log.trace("video   " + (videoChanged ? "*": " ") + " [" + ((int) data.videoAddr) + "] " + data.video.toString());
//            Log.trace("font    " + (fontChanged ? "*": " ") + " [" + ((int) data.fontAddr) + "] " + data.font.toString());
//            Log.trace("palette " + (paletteChanged ? "*": " ") + " [" + ((int) data.paletteAddr) + "] " + data.palette.toString());
//
//            world.getSystem(ServerNetworkSystem.class).broadcastMonitorData(entity);
//        }
    }
}