package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;
import java.util.concurrent.Callable;
import org.megastage.client.ClientGlobals;
import org.megastage.client.controls.AxisRotationControl;
import org.megastage.client.controls.PositionControl;

public class PlayerIDMessage extends EventMessage {
    private int eid = 0;

    public PlayerIDMessage() {}
    public PlayerIDMessage(int id) {
        this.eid = id;
    }

    @Override
    public void receive(Connection pc) {
        ClientGlobals.playerEntity = eid;

        final AxisRotationControl bodyRotationControl = new AxisRotationControl(eid, false, true, false);
        final AxisRotationControl headRotationControl = new AxisRotationControl(eid, true, false, false);

        ClientGlobals.playerNode.addControl(new PositionControl(eid));
        ClientGlobals.playerNode.addControl(bodyRotationControl);
        ClientGlobals.playerNode.getChild(0).addControl(headRotationControl);
    }

    @Override
    public String toString() {
        return "PlayerIDMessage(" + eid + ")";
    }
}

