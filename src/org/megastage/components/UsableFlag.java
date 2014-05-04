package org.megastage.components;

import org.megastage.protocol.Message;

public class UsableFlag extends BaseComponent {
    @Override
    public Message replicate(int eid) {
        return always(eid);
    }
}
