package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.util.Globals;
import org.megastage.components.dcpu.VirtualMonitor;

public class VirtualMonitorSenderSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<VirtualMonitor> virtualMonitorMapper;

    public VirtualMonitorSenderSystem() {
        super(Aspect.getAspectForAll(VirtualMonitor.class));
    }

    @Override
    protected void process(Entity entity) {
        VirtualMonitor mon = virtualMonitorMapper.get(entity);
        
        boolean changed = mon.videoRAMAddr == 0 ?
                mon.videoRAM.update(VirtualMonitor.EMPTY, (char) 0, 384):
                mon.videoRAM.update(mon.dcpu.ram, mon.videoRAMAddr, 384);

        if(true) {
            world.getSystem(ServerNetworkSystem.class).sendVideoMemory(Globals.Message.VIDEO_RAM, entity, mon.videoRAM.mem);
        }
    }
}