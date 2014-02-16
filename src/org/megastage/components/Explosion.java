package org.megastage.components;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.client.ClientGlobals;
import org.megastage.protocol.Network;
import org.megastage.util.Mapper;
import org.megastage.util.Time;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Explosion extends BaseComponent {
    public long startTime = Time.value;
    
    public transient int clientState = -1;
    public int serverState = 0;

    @Override
    public void receive(Connection pc, Entity entity) {
        if(serverState == 0) {
            super.receive(pc, entity);

            ClientGlobals.spatialManager.setupExplosion(entity, this);
            
        } else {
            Explosion expl = Mapper.EXPLOSION.get(entity);
            expl.serverState = serverState;
        }
    }

    @Override
    public boolean synchronize() {
        return clientState != serverState;
    }

    @Override
    public Network.ComponentMessage create(Entity entity) {
        clientState = serverState;
        return new Network.ComponentMessage(entity, copy());
    }
    
    public BaseComponent copy() {
        Explosion expl = new Explosion();
        expl.serverState = serverState;
        return expl;
    }
}
