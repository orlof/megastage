package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;

public class PlayerIDMessage extends EventMessage {
    private int eid = 0;

    public PlayerIDMessage() {}
    public PlayerIDMessage(int id) {
        this.eid = id;
    }

    @Override
    public void receive(Connection pc) {
        ClientGlobals.playerEntity = eid;
    }

    @Override
    public String toString() {
        return "PlayerIDMessage(" + eid + ")";
    }
}

