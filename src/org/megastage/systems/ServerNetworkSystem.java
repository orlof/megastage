package org.megastage.systems;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.Bag;
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
import java.util.ArrayList;
import org.megastage.components.BaseComponent;
import org.megastage.components.dcpu.VirtualMonitor;
import org.megastage.components.server.BindTo;
import org.megastage.server.TemplateManager;

public class ServerNetworkSystem extends VoidEntitySystem {
    private Server server;
    
    private long timeOfLastSync;
    private long interval;

    public ServerNetworkSystem(long interval) {
        this.interval = interval;
    }
    
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
    protected void processSystem() {
        timeOfLastSync = Globals.time;
        broadcastTimeData();
    }

    @Override
    protected boolean checkProcessing() {
        return (timeOfLastSync + interval) < Globals.time;
    }

    private void handleLogoutMessage(PlayerConnection connection, Network.Logout packet) {
        world.deleteEntity(connection.player);
        connection.close();
    }

    private void handleLoginMessage(PlayerConnection connection, Network.Login packet) {
        connection.player = world.createEntity();
        connection.player.addToWorld();
        
        Entity ship = world.getManager(TemplateManager.class).create("Apollo 13");
        ship.addToWorld();
        
        connection.player.addComponent(new BindTo(ship));

        connection.sendTCP(new Network.LoginResponse(ship.getId()));

        ImmutableBag<Entity> entities = world.getManager(GroupManager.class).getEntities("client");
        Log.info("Sending intialization data for " + entities.size() + " entities.");

        for(int i=0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            Log.debug("Initializing " + entity.toString());
            
            Bag<Component> components = entity.getComponents(new Bag<Component>());
            ArrayList list = new ArrayList();

            for(int j=0; j < components.size(); j++) {
                BaseComponent comp = (BaseComponent) components.get(j);
                Log.debug(" Component " + comp.toString());

                Object trans = comp.create(entity);                
                if(trans != null) {
                    Log.debug("   Added");
                    list.add(trans);
                }
            }

            if(!list.isEmpty()) {
                connection.sendTCP(list.toArray());
            }
        }
    }

    /*
    private void handleUseMessage(PlayerConnection connection, UseData packet) {
        Entity item = world.getEntity(packet.entityID);
        Entity player = connection.player;

        ItemInUse comp = new ItemInUse();
        comp.entity = item;

        player.addComponent(comp);
    }
*/
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
        VirtualMonitor mon = entity.getComponent(VirtualMonitor.class);
        server.sendToAllUDP(mon.create(entity));
    }
    
    public void broadcastTimeData() {
        Network.TimeData data = new Network.TimeData();
        server.sendToAllUDP(data);
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

            } else if(o instanceof Network.KeyEvent) {
                handleKeyEventMessage(pc, (Network.KeyEvent) o);
            }
        }
    }
}

