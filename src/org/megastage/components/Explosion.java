package org.megastage.components;

import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;

public class Explosion extends ReplicatedComponent {
    public transient long startTime = World.INSTANCE.time;

    public int state = -1;

    @Override
    public void receive(int eid) {
        boolean noExplosion = !World.INSTANCE.hasComponent(eid, CompType.Explosion);

        World.INSTANCE.setComponent(eid, this);
        
        if(noExplosion) {
            ClientGlobals.spatialManager.setupExplosion(eid);
        }
    }

    public void setState(int state) {
        if(this.state != state) {
            this.state = state;
            dirty = true;
        }
    }
}
