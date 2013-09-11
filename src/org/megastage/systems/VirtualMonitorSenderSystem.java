package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.util.Globals;
import org.megastage.components.dcpu.VirtualMonitor;
import org.megastage.util.application.Log;

import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualMonitorSenderSystem extends EntityProcessingSystem {
    private final static Logger LOG = Logger.getLogger(VirtualMonitorSenderSystem.class.getName());

    @Mapper ComponentMapper<VirtualMonitor> virtualMonitorMapper;

    public VirtualMonitorSenderSystem() {
        super(Aspect.getAspectForAll(VirtualMonitor.class));
    }

    @Override
    protected void process(Entity entity) {
        LOG.finer(entity.toString());

        VirtualMonitor mon = virtualMonitorMapper.get(entity);

        boolean changed = mon.videoRAMAddr == 0 ?
                mon.videoRAM.update(VirtualMonitor.EMPTY, (char) 0, 384):
                mon.videoRAM.update(mon.dcpu.ram, mon.videoRAMAddr, 384);

        if(changed) {
            world.getSystem(ServerNetworkSystem.class).sendMemory(Globals.Message.VIDEO_RAM, entity, mon.videoRAM.mem);
        }
        
        changed = mon.fontRAMAddr == 0 ?
                mon.fontRAM.update(VirtualMonitor.defaultFont):
                mon.fontRAM.update(mon.dcpu.ram, mon.fontRAMAddr, 256);

        if(changed) {
            world.getSystem(ServerNetworkSystem.class).sendMemory(Globals.Message.FONT_RAM, entity, mon.fontRAM.mem);
        }

        changed = mon.paletteRAMAddr == 0 ?
                mon.paletteRAM.update(VirtualMonitor.defaultPalette):
                mon.paletteRAM.update(mon.dcpu.ram, mon.paletteRAMAddr, 16);

        if(changed) {
            world.getSystem(ServerNetworkSystem.class).sendMemory(Globals.Message.PALETTE_RAM, entity, mon.paletteRAM.mem);
        }

    }
}