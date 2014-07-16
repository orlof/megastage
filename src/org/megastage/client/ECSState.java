package org.megastage.client;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import org.megastage.ecs.World;
import org.megastage.systems.client.ClientMonitorRenderSystem;
import org.megastage.systems.client.ClientNetworkSystem;
import org.megastage.systems.client.EntityDeleteSystem;

public class ECSState extends AbstractAppState {
    public World world;

    public ECSState() {
        setEnabled(false);
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        world = new World();

        //world.addProcessor(new ImposterSystem(world, 1000));
        world.addProcessor(new ClientMonitorRenderSystem(world, 0));
        //world.addProcessor(new OrbitalMovementSystem(world, 0));
        //world.addProcessor(new ClientFixedRotationSystem(world, 0));

        ClientGlobals.network = new ClientNetworkSystem(world, 20);
        world.addProcessor(ClientGlobals.network);

        world.addProcessor(new EntityDeleteSystem(world, 0));

        world.initialize();

        ClientGlobals.network.sendLogin();
        //world.getSystem(ClientNetworkSystem.class).sendUseEntity();
    }

    @Override
    public void update(float tpf) {
        world.tick(System.currentTimeMillis());
    }

    @Override
    public void cleanup() {
        world.getProcessor(ClientNetworkSystem.class).sendLogout();
    }
}
