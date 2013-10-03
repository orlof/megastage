package org.megastage.systems;

import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.megastage.components.ItemInUse;
import org.megastage.components.dcpu.VirtualKeyboard;
import org.megastage.components.dcpu.VirtualMonitor;
import org.megastage.protocol.Network;
import org.megastage.protocol.PlayerConnection;
import org.megastage.util.Globals;

import java.io.IOException;
import java.util.logging.Logger;

public class ServerNetworkSystem extends VoidEntitySystem {
    private final static Logger LOG = Logger.getLogger(ServerNetworkSystem.class.getName());

    private Server server;

    public ServerNetworkSystem() {
        super();

        server = new Server() {
            protected Connection newConnection () {
                // By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up.
                return new PlayerConnection();
            }
        };

        Network.register(server);

        server.addListener(new ServerNetworkListener());

        try {
            server.bind(Globals.serverPort, Globals.serverPort + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.start();
    }

    @Override
    protected void processSystem() {
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }

    private void handleLoginMessage(PlayerConnection connection, Network.Login packet) {
        Entity player = world.createEntity();
        player.addToWorld();

        connection.sendTCP(Network.LoginResponse.create(player));

        unicastGroupData(connection, "star", new PacketFactory() {
            public Object create(Entity entity) {
                return Network.StarData.create(entity);
            }
        });

        unicastGroupData(connection, "monitor", new PacketFactory() {
            public Object create(Entity entity) {
                return Network.MonitorData.create(entity);
            }
        });
    }

    private void handleUseMessage(PlayerConnection connection, Network.Use packet) {
        Entity item = world.getEntity(packet.entityID);
        Entity player = connection.player;

        ItemInUse comp = new ItemInUse();
        comp.entity = item;

        player.addComponent(comp);
    }

    private void handleKeyEventMessage(PlayerConnection connection, Network.KeyEvent packet) {
        ItemInUse item = connection.player.getComponent(ItemInUse.class);
        VirtualKeyboard kbd = item.entity.getComponent(VirtualKeyboard.class);

        if(packet instanceof Network.KeyTyped) {
            kbd.keyTyped(packet.key);

        } else if(packet instanceof Network.KeyPressed) {
            kbd.keyPressed(packet.key);

        } else if(packet instanceof Network.KeyReleased) {
            kbd.keyReleased(packet.key);
        }
    }

    public void broadcastMonitorData(Entity entity) {
        Network.MonitorData monitorData = new Network.MonitorData();
        monitorData.entityID = entity.getId();
        monitorData.monitor = entity.getComponent(VirtualMonitor.class);

        server.sendToAllUDP(monitorData);
    }

    private void unicastGroupData(PlayerConnection connection, String group, PacketFactory factory) {
        ImmutableBag<Entity> entities = world.getManager(GroupManager.class).getEntities(group);
        for(int i=0; i < entities.size(); i++) {
            connection.sendTCP(factory.create(entities.get(i)));
        }
    }

    private interface PacketFactory {
        Object create(Entity entity);
    }

    private class ServerNetworkListener extends Listener {
        @Override
        public void connected(Connection connection) {
            super.connected(connection);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void disconnected(Connection connection) {
            super.disconnected(connection);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void received(Connection connection, Object o) {
            PlayerConnection pc = (PlayerConnection) connection;

            if(o instanceof Network.Login) {
                handleLoginMessage(pc, (Network.Login) o);

            } else if(o instanceof Network.Use) {
                handleUseMessage(pc, (Network.Use) o);

            } else if(o instanceof Network.KeyEvent) {
                handleKeyEventMessage(pc, (Network.KeyEvent) o);
            }
        }
    }
}

