/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import org.megastage.systems.ClientEntityManagerSystem;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import org.megastage.systems.ClientFixedRotationSystem;
import org.megastage.systems.ClientMonitorRenderSystem;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.systems.ClientOrbitalMovementSystem;
import org.megastage.systems.ClientSpatialManagerSystem;
import org.megastage.systems.OrbitalMovementSystem;
import org.megastage.util.ClientGlobals;

/**
 *
 * @author Teppo
 */
public class ArtemisState extends AbstractAppState {
    World world;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        world = new World();

        world.setSystem(new ClientSpatialManagerSystem((SimpleApplication) app), true);
        world.setSystem(new ClientEntityManagerSystem(), true);
        //world.setSystem(new ClientKeyboardSystem());
        
        world.setSystem(new ClientMonitorRenderSystem());
        world.setSystem(new OrbitalMovementSystem());
        world.setSystem(new ClientFixedRotationSystem());

        ClientGlobals.network = new ClientNetworkSystem(20);
        world.setSystem(ClientGlobals.network);

        world.initialize();

        ClientGlobals.network.sendLogin();
        //world.getSystem(ClientNetworkSystem.class).sendUseEntity();
    }

    @Override
    public void update(float tpf) {
        world.setDelta(tpf);

        ClientGlobals.time = System.currentTimeMillis() + ClientGlobals.timeDiff;
        
        world.process();
    }

    @Override
    public void cleanup() {
        world.getSystem(ClientNetworkSystem.class).sendLogout();
    }
    
    
}
