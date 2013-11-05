package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.components.dcpu.LEMUtil;
import org.megastage.components.dcpu.VirtualMonitor;

public class VirtualMonitorSenderSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<VirtualMonitor> virtualMonitorMapper;

    public VirtualMonitorSenderSystem() {
        super(Aspect.getAspectForAll(VirtualMonitor.class));
    }

    @Override
    protected void process(Entity entity) {
        VirtualMonitor mon = virtualMonitorMapper.get(entity);

        boolean videoChanged = mon.videoAddr == 0 ?
                mon.video.update(LEMUtil.defaultVideo):
                mon.video.update(mon.dcpu.ram, mon.videoAddr, 384);

        boolean fontChanged = mon.fontAddr == 0 ?
                mon.font.update(LEMUtil.defaultFont):
                mon.font.update(mon.dcpu.ram, mon.fontAddr, 256);

        boolean paletteChanged = mon.paletteAddr == 0 ?
                mon.palette.update(LEMUtil.defaultPalette):
                mon.palette.update(mon.dcpu.ram, mon.paletteAddr, 16);

        if(videoChanged || fontChanged || paletteChanged) {
            world.getSystem(ServerNetworkSystem.class).broadcastMonitorData(entity);
        }

    }
}