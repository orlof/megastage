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
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import java.util.HashMap;
import org.megastage.systems.ClientFixedRotationSystem;
import org.megastage.systems.ClientMonitorRenderSystem;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.systems.ClientOrbitalMovementSystem;
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
    
    HashMap<Integer, Entity> serverIDToClientEntity = new HashMap<>();
    HashMap<Integer, Integer> clientIDToServerID = new HashMap<>();

    public Entity get(int serverEntityID) {
        Entity entity = serverIDToClientEntity.get(serverEntityID);
        if(entity == null) {
            entity = create(serverEntityID);
        }
        return entity;
    }

    public <T extends Component> T getComponent(Entity entity, Class<T> type) {
        T component = entity.getComponent(type);
        if(component == null) {
            component = createComponent(entity, type);
        }
        return type.cast(component);
    }

    public <U extends Component> U getComponent(int serverID, Class<U> clazz) {
        return getComponent(get(serverID), clazz);
    }

    public <T extends Component> void setComponent(int entityID, T t) {
        Log.debug("Add component " + t.toString());
        get(entityID).addComponent(t);
    }

    public <T extends Component> void setComponent(Entity entity, T t) {
        Log.debug(entity.getId() + " <- setComponent(" + t.toString() + ")");
        entity.addComponent(t);
    }

    public int convert(int clientID) {
        return clientIDToServerID.get(clientID);
    }
        
    private Entity create(int serverEntityID) {
        Entity entity = world.createEntity();
        world.addEntity(entity);
        serverIDToClientEntity.put(serverEntityID, entity);
        clientIDToServerID.put(entity.getId(), serverEntityID);

        Log.info("Created new entity " + serverEntityID + " -> " + entity.toString());
        
        return entity;
    }

    private <T extends Component> T createComponent(Entity entity, Class<T> type) {
        Log.info(entity.getId() + " <- CreateComponent(" + type.toString() + ")");
        T component = null;
        
        try {
            component = type.newInstance();
            entity.addComponent(component);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return component;
    }
    
}
