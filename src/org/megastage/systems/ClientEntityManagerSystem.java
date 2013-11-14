/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.systems;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.systems.VoidEntitySystem;
import com.esotericsoftware.minlog.Log;
import java.util.HashMap;

/**
 *
 * @author Teppo
 */
public class ClientEntityManagerSystem extends VoidEntitySystem  {
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
        Log.info("Add component " + t.toString());
        get(entityID).addComponent(t);
    }

    public <T extends Component> void setComponent(Entity entity, T t) {
        Log.info("Add component " + t.toString());
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

        Log.info("Created new entity " + entity.toString());
        
        return entity;
    }

    private <T extends Component> T createComponent(Entity entity, Class<T> type) {
        Log.info("CreateComponent " + type.toString());
        T component = null;
        
        try {
            component = type.newInstance();
            entity.addComponent(component);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return component;
    }

    @Override
    protected void processSystem() {
        // Intentionally left empty
    }
}
