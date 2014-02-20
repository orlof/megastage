package org.megastage.components;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.protocol.Message;
import org.megastage.protocol.Network.ComponentMessage;
import org.megastage.util.Mapper;
import org.megastage.util.Time;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Explosion extends BaseComponent {
    public transient long startTime = Time.value;
    public transient boolean dirty = true;

    public int state = 0;

    @Override
    public void receive(Connection pc, Entity entity) {
        Explosion comp = Mapper.EXPLOSION.get(entity);
        if(comp == null) {
            entity.addComponent(this);
            entity.changedInWorld();

            ClientGlobals.spatialManager.setupExplosion(entity, this);
        } else {
            comp.state = state;
        }
    }

    @Override
    public Message synchronize(Entity entity) {
        if(dirty) {
            dirty = false;
            return new ComponentMessage(entity, this);
        }

        return null;
    }
}
