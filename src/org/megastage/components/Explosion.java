package org.megastage.components;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.util.ID;

public class Explosion extends BaseComponent {
    public long startTime = World.INSTANCE.time;

    public int state = -1;

    @Override
    public void receive(World world, Connection pc, int eid) {
        Log.info(ID.get(eid) + this.toString());
        Explosion comp = (Explosion) world.getComponent(eid, CompType.Explosion);
        if(comp == null) {
            world.addComponent(eid, this);
            ClientGlobals.spatialManager.setupExplosion(eid, this);
        } else {
            comp.setState(state);
        }
    }

    @Override
    public Message synchronize(int eid) {
        return ifDirty(eid);
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
