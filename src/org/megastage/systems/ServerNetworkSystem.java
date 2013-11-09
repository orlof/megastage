package org.megastage.systems;

import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.ItemInUse;
import org.megastage.components.dcpu.VirtualKeyboard;
import org.megastage.protocol.Network;
import org.megastage.protocol.PlayerConnection;
import org.megastage.util.Globals;

import java.io.IOException;

public class ServerNetworkSystem extends VoidEntitySystem {
    private Server server;

    @Override
    protected void initialize() {
        server = new Server() {
            protected Connection newConnection () {
                // By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up.
                return new PlayerConnection();
            }
        };

        Network.register(server);

        new Thread(server).start();

        server.addListener(new ServerNetworkListener());

        try {
            server.bind(Globals.serverPort, Globals.serverPort + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void processSystem() {}

    @Override
    protected boolean checkProcessing() {
        return false;
    }

    private void handleLogoutMessage(PlayerConnection connection, Network.Logout packet) {
        world.deleteEntity(connection.player);
        connection.close();
    }

    private void handleLoginMessage(PlayerConnection connection, Network.Login packet) {
        connection.player = world.createEntity();
        connection.player.addToWorld();

        connection.sendTCP(new Network.LoginResponse());

        unicastGroupData(connection, "star", new PacketFactory() {
            public Object create(Entity entity) {
                return new Object[] {
                    Network.SpatialSphereData.create(entity),
                    Network.PositionData.create(entity),
                    Network.MassData.create(entity)
                };
            }
        });

        unicastGroupData(connection, "satellite", new PacketFactory() {
            public Object create(Entity entity) {
                return new Object[] {
                    Network.SpatialSphereData.create(entity),
                    Network.OrbitData.create(entity),
                    Network.MassData.create(entity),
                    Network.PositionData.create(entity)
                };
            }
        });

        unicastGroupData(connection, "monitor", new PacketFactory() {
            public Object create(Entity entity) {
                return new Object[] {
                    Network.SpatialMonitorData.create(entity),
                    Network.MonitorData.create(entity),
                    Network.PositionData.create(entity)
                };
            }
        });

        unicastGroupData(connection, "keyboard", new PacketFactory() {
            public Object create(Entity entity) {
                return Network.KeyboardData.create(entity);
            }
        });
    }

    private void handleUseMessage(PlayerConnection connection, Network.UseData packet) {
        Entity item = world.getEntity(packet.entityID);
        Entity player = connection.player;

        ItemInUse comp = new ItemInUse();
        comp.entity = item;

        player.addComponent(comp);
    }

    private void handleKeyEventMessage(PlayerConnection connection, Network.KeyEvent packet) {
        ItemInUse item = connection.player.getComponent(ItemInUse.class);
        if(item == null) {
            return;
        }

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
        Network.MonitorData monitorData = Network.MonitorData.create(entity);
        server.sendToAllUDP(monitorData);
    }
    
    public void broadcastTimeData() {
        Network.TimeData data = Network.TimeData.create();
        server.sendToAllUDP(data);
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
        }

        @Override
        public void disconnected(Connection connection) {
        }

        @Override
        public void received(Connection connection, Object o) {
            Log.info("Received: " + o.getClass().getName());
            PlayerConnection pc = (PlayerConnection) connection;

            if(o instanceof Network.Login) {
                handleLoginMessage(pc, (Network.Login) o);

            } else if(o instanceof Network.Logout) {
                handleLogoutMessage(pc, (Network.Logout) o);

            } else if(o instanceof Network.UseData) {
                handleUseMessage(pc, (Network.UseData) o);

            } else if(o instanceof Network.KeyEvent) {
                handleKeyEventMessage(pc, (Network.KeyEvent) o);
            }
        }
    }
}

