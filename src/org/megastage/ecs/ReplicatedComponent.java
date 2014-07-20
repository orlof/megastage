package org.megastage.ecs;

import org.megastage.protocol.Message;
import org.megastage.protocol.Network;

public abstract class ReplicatedComponent extends BaseComponent {
    protected transient boolean dirty = true;

    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    public boolean isReplicable() {
        return true;
    }
    
    public Message synchronize(int eid) {
        return new Network.ComponentMessage(eid, this);
    }

    public void receive(int eid) {
        World.INSTANCE.setComponent(eid, this);
    }
}
