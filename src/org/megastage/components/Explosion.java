package org.megastage.components;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.util.Time;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Explosion extends BaseComponent {
    public long startTime = Time.value;
    public int state = 0;

    @Override
    public void receive(Connection pc, Entity entity) {
        Explosion expl = entity.getComponent(Explosion.class);
        
        if(expl == null) {
            ClientGlobals.spatialManager.setupExplosion(entity, this);
        }
        
        entity.addComponent(this);
    }

    public BaseComponent copy() {
        Explosion expl = new Explosion();
        expl.state = state;
        return expl;
    }

}
