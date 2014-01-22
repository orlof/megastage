/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import java.util.HashMap;
import org.megastage.systems.ClientFixedRotationSystem;
import org.megastage.systems.ClientMonitorRenderSystem;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.systems.ClientOrbitalMovementSystem;
import org.megastage.systems.OrbitalMovementSystem;

/**
 *
 * @author Orlof
 */
public class ArtemisState extends AbstractAppState {
    public World world;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        world = new World();

        world.setSystem(new ClientMonitorRenderSystem(), false);
        world.setSystem(new ClientOrbitalMovementSystem());
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
    
    // TODO following entity management should be cleaned so that
    // server-client entity id conversion is done only in network interface
    
    HashMap<Integer, Entity> serverToClient = new HashMap<>();
    HashMap<Integer, Integer> clientToServer = new HashMap<>();

    public <T extends Component> T getComponent(Entity entity, Class<T> type) {
        T component = entity.getComponent(type);
        if(component == null) {
            component = createComponent(entity, type);
        }
        return type.cast(component);
    }

    public int toServerID(int clientID) {
        return clientToServer.get(clientID);
    }
        
    public Entity toClientEntity(int serverID) {
        Entity entity = serverToClient.get(serverID);
        if(entity == null) {
            entity = addEntity(serverID);
        }
        return entity;
    }
        
    private Entity addEntity(int serverID) {
        Entity entity = world.createEntity();
        world.addEntity(entity);

        serverToClient.put(serverID, entity);
        clientToServer.put(entity.getId(), serverID);

        Log.info("Created new entity " + serverID + " -> " + entity.getId());
        
        return entity;
    }

    private <T extends Component> T createComponent(Entity entity, Class<T> type) {
        Log.info(entity.toString() + " <- " + type.getSimpleName());
        T component = null;
        
        try {
            component = type.newInstance();
            entity.addComponent(component);
            world.changedEntity(entity);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return component;
    }
    
}
