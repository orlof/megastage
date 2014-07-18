package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.client.SpatialManager;
import org.megastage.client.controls.AxisRotationControl;
import org.megastage.client.controls.LocalPositionControl;
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
        ClientGlobals.playerEntity = eid;

        final AxisRotationControl bodyRotationControl = new AxisRotationControl(eid, false, true, false);
        final AxisRotationControl headRotationControl = new AxisRotationControl(eid, true, false, false);

        ClientGlobals.playerNode.addControl(new LocalPositionControl(eid));
        ClientGlobals.playerNode.addControl(bodyRotationControl);
        ClientGlobals.playerNode.getChild(0).addControl(headRotationControl);

        BindTo bindTo = (BindTo) World.INSTANCE.getComponent(eid, CompType.BindTo);
        if(bindTo != null) {
            SpatialManager.changeShip(bindTo.parent);
        }
    }

    @Override
    public String toString() {
        return "PlayerIDMessage(" + eid + ")";
    }
}

