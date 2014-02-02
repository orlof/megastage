package org.megastage.components;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.protocol.Network;
import org.megastage.util.Time;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Explosion extends BaseComponent {
    public long startTime = Time.value;
    
    public int clientState = -1;
    public int serverState = 0;

    @Override
    public void receive(Connection pc, Entity entity) {
        if(serverState == 0) {
            entity.addComponent(this);
            entity.changedInWorld();

            ClientGlobals.spatialManager.setupExplosion(entity, this);
            
        } else {
            Explosion expl = entity.getComponent(Explosion.class);
            expl.serverState = serverState;
        }

        Log.info("received explosion "+entity.toString()+" state: " + serverState);
    }

    @Override
    public boolean synchronize() {
        return clientState != serverState;
    }

    @Override
    public Network.ComponentMessage create(Entity entity) {
        Log.info(entity.toString() + " clientState from " + clientState + " to " + serverState);

        clientState = serverState;
        return new Network.ComponentMessage(entity, copy());
    }
    
    public BaseComponent copy() {
        Explosion expl = new Explosion();
        expl.serverState = serverState;
        return expl;
    }
}
