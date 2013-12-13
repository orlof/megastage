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
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.dcpu.VirtualMonitor;
import org.megastage.components.server.BindTo;
import org.megastage.components.server.ShipGeometry;
import org.megastage.protocol.LoginResponse;
import org.megastage.protocol.UserCommand;
import org.megastage.server.TemplateManager;
import org.megastage.util.ClientGlobals;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector;

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
            @Override
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
            server.bind(Network.serverPort, Network.serverPort + 1);
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
        BindTo bindTo = connection.player.getComponent(BindTo.class);
        if(bindTo != null) {
            Entity e = world.getEntity(bindTo.entityID);
            world.deleteEntity(e);
        }
        world.deleteEntity(connection.player);
        connection.close();
    }

    private void handleLoginMessage(PlayerConnection connection, Network.Login packet) {
        // create player
        connection.player = world.getManager(TemplateManager.class).create("Player");
        connection.player.addToWorld();
        
        // create ship
        Entity ship = world.getManager(TemplateManager.class).create("Apollo 13");
        ship.addToWorld();
        
        // bind player to ship
        BindTo bind = new BindTo();
        bind.entityID = ship.getId();
        connection.player.addComponent(bind);

        ShipGeometry sg = ship.getComponent(ShipGeometry.class);
        
        Position pos = connection.player.getComponent(Position.class);
        pos.x = 2000 * sg.entry_x;
        pos.y = 2000 * sg.entry_y;
        pos.z = 2000 * sg.entry_z;
        
        connection.sendTCP(new LoginResponse(connection.player.getId()));

        ImmutableBag<Entity> entities = world.getManager(GroupManager.class).getEntities("client");
        Log.info("Sending intialization data for " + entities.size() + " entities.");

        for(int i=0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            sendComponents(connection, entity);
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
        ImmutableBag<Entity> entities = world.getManager(GroupManager.class).getEntities("keyboard");
        Entity entity = entities.get(0);

        VirtualKeyboard kbd = entity.getComponent(VirtualKeyboard.class);

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

    private void sendComponents(PlayerConnection connection, Entity entity) {
        Log.debug("Sending components for " + entity.toString());

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
    
    private void handleUserCmd(PlayerConnection connection, UserCommand cmd) {
        if(connection.player == null) return;
        
        Position pos = connection.player.getComponent(Position.class);
        pos.x += 1000 * cmd.xMove;
        pos.z += 1000 * cmd.zMove;

        connection.sendUDP(pos.create(connection.player));
        
        BindTo bindTo = connection.player.getComponent(BindTo.class);
        Entity ship = world.getEntity(bindTo.entityID);
        
        Rotation shipRotation = ship.getComponent(Rotation.class);
        Quaternion shipRotationQuaternion = shipRotation.getQuaternion();
        
        Vector vel = new Vector(cmd.shipLeft, cmd.shipUp, cmd.shipForward).multiply(shipRotationQuaternion);
        
        vel = vel.multiply(50000000);
        
        Position shipPos = ship.getComponent(Position.class);
        shipPos.x += vel.x;
        shipPos.y += vel.y;
        shipPos.z += vel.z;

        connection.sendUDP(shipPos.create(ship));
        
        // rotate rotation axis by fixedEntity rotation
        Vector yAxis = new Vector(0, 1, 0).multiply(shipRotationQuaternion);
        Quaternion yRotation = new Quaternion(yAxis, cmd.shipYaw);
        
        Vector zAxis = new Vector(0, 0, 1).multiply(shipRotationQuaternion);
        Quaternion zRotation = new Quaternion(zAxis, cmd.shipRoll);

        Vector xAxis = new Vector(1, 0, 0).multiply(shipRotationQuaternion);
        Quaternion xRotation = new Quaternion(xAxis, cmd.shipPitch);

        shipRotationQuaternion = yRotation.multiply(shipRotationQuaternion).normalize();
        shipRotationQuaternion = zRotation.multiply(shipRotationQuaternion).normalize();
        shipRotationQuaternion = xRotation.multiply(shipRotationQuaternion).normalize();
        
        shipRotation.x = shipRotationQuaternion.x;
        shipRotation.y = shipRotationQuaternion.y;
        shipRotation.z = shipRotationQuaternion.z;
        shipRotation.w = shipRotationQuaternion.w;

        connection.sendUDP(shipRotation.create(ship));
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
            Log.debug("Received: " + o.getClass().getName());
            PlayerConnection pc = (PlayerConnection) connection;

            if(o instanceof Network.Login) {
                handleLoginMessage(pc, (Network.Login) o);

            } else if(o instanceof Network.Logout) {
                handleLogoutMessage(pc, (Network.Logout) o);

            } else if(o instanceof Network.KeyEvent) {
                handleKeyEventMessage(pc, (Network.KeyEvent) o);
            
            } else if(o instanceof UserCommand) {
                handleUserCmd(pc, (UserCommand) o);
            }
        }
    }
}

