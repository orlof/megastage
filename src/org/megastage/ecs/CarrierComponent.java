package org.megastage.ecs;

import org.megastage.protocol.ComponentMessage;
import org.megastage.protocol.Message;

public abstract class CarrierComponent extends DirtyComponent implements Message {
    /** This message is called when server wraps this component for sending to client as status update */
    public Message replicate(int eid) {
        dirty = false;
        return new ComponentMessage(eid, this);
    }
}
