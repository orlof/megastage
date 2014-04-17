package org.megastage.components;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.client.ClientGlobals;
import org.megastage.protocol.Message;
import org.megastage.protocol.Network.ComponentMessage;
import org.megastage.util.ID;
import org.megastage.util.Mapper;
import org.megastage.util.GlobalTime;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Explosion extends BaseComponent {
    public long startTime = GlobalTime.value;

    public int state = -1;

    @Override
    public void receive(Connection pc, Entity entity) {
        Log.info(ID.get(entity) + this.toString());
        Explosion comp = Mapper.EXPLOSION.get(entity);
        if(comp == null) {
            entity.addComponent(this);
            entity.changedInWorld();

            ClientGlobals.spatialManager.setupExplosion(entity, this);
        } else {
            comp.setState(state);
        }
    }

    @Override
    public Message synchronize(Entity entity) {
        return ifDirty(entity);
    }

    public void setState(int currentState) {
        if(currentState != state) {
            state = currentState;
            dirty = true;
        }
    }
    
    public String toString() {
        return "Explosion[state=" + state + "]";
    }
}
