/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import java.util.HashMap;
import org.megastage.systems.client.ClientFixedRotationSystem;
import org.megastage.systems.client.ClientMonitorRenderSystem;
import org.megastage.systems.client.ClientNetworkSystem;
import org.megastage.systems.OrbitalMovementSystem;
import org.megastage.systems.client.ImposterSystem;
import org.megastage.util.Mapper;
import org.megastage.util.Time;

/**
 *
 * @author Orlof
 */
public class ArtemisState extends AbstractAppState {
    public World world;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        world = new World();

        world.setSystem(new ImposterSystem(1000));
        world.setSystem(new ClientMonitorRenderSystem());
        world.setSystem(new OrbitalMovementSystem());
        world.setSystem(new ClientFixedRotationSystem());

        ClientGlobals.network = new ClientNetworkSystem(20);
        world.setSystem(ClientGlobals.network);

        world.initialize();
        Mapper.init(world);

        ClientGlobals.network.sendLogin();
        //world.getSystem(ClientNetworkSystem.class).sendUseEntity();
    }

    @Override
    public void update(float tpf) {
        world.setDelta(tpf);
        Time.value = System.currentTimeMillis() + ClientGlobals.timeDiff;
        
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
        clientToServer.put(entity.id, serverID);

        return entity;
    }

    private <T extends Component> T createComponent(Entity entity, Class<T> type) {
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
