package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;
import com.jme3.scene.Node;
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

        setPlayerControlsToNode(ClientGlobals.playerNode);
    }

    @Override
    public String toString() {
        return "PlayerIDMessage(" + eid + ")";
    }

    private void setPlayerControlsToNode(Node playerNode) {
        AxisRotationControl bodyRotationControl = new AxisRotationControl(eid, false, true, false);
        AxisRotationControl headRotationControl = new AxisRotationControl(eid, true, false, false);

        playerNode.addControl(new PositionControl(eid));
        playerNode.addControl(bodyRotationControl);
        playerNode.getChild(0).addControl(headRotationControl);
    }
}

