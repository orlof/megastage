package org.megastage.ecs;

import org.megastage.protocol.ComponentMessage;
import org.megastage.protocol.Message;

public abstract class DirtyComponent extends BaseComponent {
    protected transient boolean dirty = true;

    /** This message is called when server wraps this component for sending to client as status update */
    public Message replicate(int eid) {
        if(dirty) {
            dirty = false;
            return replicateIfDirty(eid);
        }

        return null;
    }

    protected Message replicateIfDirty(int eid) {
        return new ComponentMessage(eid, this);
    }

    @Override
    public void receive(int eid) {
        World.INSTANCE.setComponent(eid, CompType.getCID(this), this);
    }
}
