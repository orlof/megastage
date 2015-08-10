package org.megastage.systems.srv;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.megastage.components.BindTo;
import org.megastage.components.device.InterfaceKeyboard;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.components.srv.BlockChanges;
import org.megastage.ecs.CompType;
import org.megastage.protocol.Carrier;
import org.megastage.protocol.PlayerConnection;
import org.megastage.protocol.UserCommand;
import org.megastage.util.ID;
import org.megastage.util.Log;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NetworkListener extends Listener {
    private static class ReceivedMessage {
        public Carrier carrier;
        public PlayerConnection conn;

        public ReceivedMessage(PlayerConnection conn, Carrier carrier) {
            this.carrier = carrier;
            this.conn = conn;
        }

        public void receive() {
            carrier.receive(conn);
        }
    }

    private List<ReceivedMessage> received = Collections.synchronizedList(new LinkedList());

    @Override
    public void connected(Connection connection) {
    }

    @Override
    public void disconnected(Connection connection) {
    }

    @Override
    public void received(Connection connection, Object msg) {
        PlayerConnection pc = (PlayerConnection) connection;
        Carrier carrier = (Carrier) msg;
        received.add(new ReceivedMessage(pc, carrier));
    }

    public void handleMessages() {
        while(!received.isEmpty()) {
            ReceivedMessage msg = received.remove(0);
            msg.receive();
        }
    }

    private void handleUserCmd(PlayerConnection connection, UserCommand cmd) {
        // TODO check player mode
        if(connection.player == 0) return;

        BindTo bindTo = (BindTo) world.getComponent(connection.player, CompType.BindTo);
        if(bindTo.parent == 0) return;

        ShipGeometry geom = (ShipGeometry) world.getComponent(bindTo.parent, CompType.ShipGeometry);

        if(cmd.move.lengthSquared() > 0) {
            updatePlayerPosition(geom.ship, connection.player, cmd);
        }
        updatePlayerRotation(connection.player, cmd);

        if(cmd.ship != null) {
            updateShip(bindTo.parent, cmd);
        }

        if(cmd.pick != null) {
            pickItem(connection, cmd);
        }

        if(cmd.unpick != null) {
            unpickItem(connection, cmd);
        }

        if(cmd.build != null) {
            BlockChanges changes = (BlockChanges) world.getComponent(bindTo.parent, CompType.BlockChanges);
            build(connection, cmd.build, geom.ship, changes);
        }

        if(cmd.unbuild != null) {
            BlockChanges changes = (BlockChanges) world.getComponent(bindTo.parent, CompType.BlockChanges);
            unbuild(connection, cmd.unbuild, geom.ship, changes);
        }

        if(cmd.teleport != null) {
            teleport(connection, cmd.teleport);
        }

        if(cmd.cmdText != null) {
            cmdText(connection, cmd.cmdText);
        }

        if(cmd.floppy != null) {
            changeFloppy(connection, cmd.floppy);
        }

        if(cmd.bootRom != null) {
            changeBootRom(connection, cmd.bootRom);
        }

        UserCommand.Keyboard keys = cmd.keyboard;

        if(keys.keyEventPtr > 0 && connection.item >= 0) {
            Log.info(keys.toString());

            Log.info("Connection item: %d %s", connection.item, ID.get(connection.item));

            InterfaceKeyboard kbd = (InterfaceKeyboard) world.getComponent(connection.item, CompType.VirtualKeyboard);
            if(kbd == null) {
                Log.warn("No virtual keyboard to handle typing");
            } else {
                for(int i=0; i < keys.keyEventPtr; /*NOP*/ ) {
                    switch(keys.keyEvents[i++]) {
                        case 'T':
                            kbd.keyTyped(keys.keyEvents[i++]);
                            break;
                        case 'P':
                            kbd.keyPressed(keys.keyEvents[i++]);
                            break;
                        case 'R':
                            kbd.keyReleased(keys.keyEvents[i++]);
                            break;
                        default:
                            assert false;
                    }
                }
            }
        }
    }
}
