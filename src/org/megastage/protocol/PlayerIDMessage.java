package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.components.gfx.BindTo;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class PlayerIDMessage extends EventMessage {
    private int eid = 0;

    public PlayerIDMessage() {}
    public PlayerIDMessage(int id) {
        this.eid = id;
    }

    @Override
    public void receive(Connection pc) {
        ClientGlobals.setPlayer(eid);

        BindTo bindTo = (BindTo) World.INSTANCE.getComponent(eid, CompType.BindTo);
        if(bindTo != null) {
            ClientGlobals.setBase(bindTo.parent);
        }
    }

    @Override
    public String toString() {
        return "PlayerIDMessage(" + eid + ")";
    }
}

