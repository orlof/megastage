package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.ImmutableBag;
import org.megastage.components.Identifier;
import org.megastage.components.dcpu.VirtualMonitor;
import org.megastage.util.Globals;

import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides initialization data for newly joined clients
 */
public class ServerRemoteInitializationSystem extends EntitySystem {
    @Mapper ComponentMapper<VirtualMonitor> virtualMonitorMapper;

    public ServerRemoteInitializationSystem() {
        super(Aspect.getAspectForOne(VirtualMonitor.class));
    }

    public LinkedList<SocketAddress> newRemotes = new LinkedList<SocketAddress>();

    @Override
    protected void processEntities(ImmutableBag<Entity> entityImmutableBag) {
        ServerNetworkSystem networkSystem = world.getSystem(ServerNetworkSystem.class);

        for(int i=0; i < entityImmutableBag.size(); i++) {
            Entity entity = entityImmutableBag.get(i);
            VirtualMonitor mon = virtualMonitorMapper.get(entity);

            for(SocketAddress remote: newRemotes) {
                networkSystem.sendVirtualMonitorData(remote, entity, mon);
            }
        }

        newRemotes.clear();
    }

    @Override
    protected boolean checkProcessing() {
        return !newRemotes.isEmpty();
    }

    public void initializeClient(SocketAddress remote) {
        newRemotes.add(remote);
    }
}