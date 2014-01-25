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
import org.megastage.components.dcpu.VirtualKeyboard;
import org.megastage.protocol.Network;
import org.megastage.protocol.PlayerConnection;

import java.io.IOException;
import java.util.ArrayList;
import org.megastage.components.BaseComponent;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.SpawnPoint;
import org.megastage.components.dcpu.VirtualMonitor;
import org.megastage.components.server.BindTo;
import org.megastage.components.server.Mode;
import org.megastage.protocol.Action;
import org.megastage.protocol.CharacterMode;
import org.megastage.protocol.LoginResponse;
import org.megastage.protocol.UserCommand;
import org.megastage.server.TemplateManager;
import org.megastage.util.Quaternion;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Vector;

public class ServerNetworkSystem extends VoidEntitySystem {
    private Server server;
    
    public ServerNetworkSystem() {
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
//            for(int i=0; i < ServerGlobals.updates.size(); i++) {
//                Log.info(ServerGlobals.updates.get(i).toString());
//            }
        
        Log.trace("Client state refreshed with packet size " + ServerGlobals.updates.size());
        server.sendToAllUDP(ServerGlobals.updates);
        ServerGlobals.updates = null;
    }

    @Override
    protected boolean checkProcessing() {
        return ServerGlobals.updates != null;
    }

    private void handleLogoutMessage(PlayerConnection connection, Network.Logout packet) {
        BindTo bindTo = connection.player.getComponent(BindTo.class);
        if(bindTo != null) {
            Entity e = world.getEntity(bindTo.parent);
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
        bind.parent = ship.getId();
        connection.player.addComponent(bind);

        SpawnPoint sp = ship.getComponent(SpawnPoint.class);
        
        Position pos = connection.player.getComponent(Position.class);
        pos.x = 1000 * sp.x;
        pos.y = 1000 * sp.y;
        pos.z = 1000 * sp.z;
        
        connection.sendTCP(new LoginResponse(connection.player.getId()));
        Log.info("Sent player entity id: " + connection.player.toString());

        ImmutableBag<Entity> entities = world.getManager(GroupManager.class).getEntities("initialization");
        Log.info("Sending initialization data for " + entities.size() + " entities.");

        for(int i=0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            sendComponents(connection, entity);
        }
    }

    private void sendComponents(PlayerConnection connection, Entity entity) {
        Log.trace("Sending components for " + entity.toString());

        Bag<Component> components = entity.getComponents(new Bag<Component>());
        ArrayList list = new ArrayList();

        for(int j=0; j < components.size(); j++) {
            BaseComponent comp = (BaseComponent) components.get(j);
            Log.trace(" Component " + comp.toString());

            Object trans = comp.create(entity);                
            if(trans != null) {
                Log.trace("   Added");
                list.add(trans);
            }
        }

        if(!list.isEmpty()) {
            connection.sendTCP(list.toArray());
        }
    }
    
    private void handleUserCmd(PlayerConnection connection, UserCommand cmd) {
        if(connection.player == null) return;
        
        Log.debug(cmd.toString());
        
        Position pos = connection.player.getComponent(Position.class);
        pos.x += 1000 * cmd.xMove;
        pos.y += 1000 * cmd.yMove;
        pos.z += 1000 * cmd.zMove;

        Rotation rot = connection.player.getComponent(Rotation.class);
        rot.x = cmd.qx;
        rot.y = cmd.qy;
        rot.z = cmd.qz;
        rot.w = cmd.qw;
        
        BindTo bindTo = connection.player.getComponent(BindTo.class);
        Entity ship = world.getEntity(bindTo.parent);
        
        Rotation shipRotation = ship.getComponent(Rotation.class);
        Quaternion shipRotationQuaternion = shipRotation.getQuaternion();
        
        Vector vel = new Vector(cmd.shipLeft, cmd.shipUp, cmd.shipForward).multiply(shipRotationQuaternion);
        
        vel = vel.multiply(50000);
        
        Position shipPos = ship.getComponent(Position.class);
        shipPos.x += vel.x;
        shipPos.y += vel.y;
        shipPos.z += vel.z;

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

        shipRotation.set(shipRotationQuaternion);

        switch(cmd.action) {
            case Action.PICK_ITEM:
                pickItem(connection, cmd);
                break;
            case Action.UNPICK_ITEM:
                unpickItem(connection, cmd);
                break;
        }

        if(cmd.keyEventPtr > 0 && connection.item != null) {
            VirtualKeyboard kbd = (VirtualKeyboard) connection.item;

            for(int i=0; i < cmd.keyEventPtr; /*NOP*/ ) {
                switch(cmd.keyEvents[i++]) {
                    case 'T':
                        kbd.keyTyped(cmd.keyEvents[i++]);
                        break;
                    case 'P':
                        kbd.keyPressed(cmd.keyEvents[i++]);
                        break;
                    case 'R':
                        kbd.keyReleased(cmd.keyEvents[i++]);
                        break;
                }
            }
        }
        
        //connection.sendUDP(shipRotation.create(ship));
    }

    private void unpickItem(PlayerConnection connection, UserCommand cmd) {
        connection.item = null;
        Mode mode = connection.player.getComponent(Mode.class);
        mode.setMode(CharacterMode.WALK);
    }
    
    private void pickItem(PlayerConnection connection, UserCommand cmd) {
        Entity target = world.getEntity(cmd.pick);
        if(target == null) {
            Log.info("pick for null item!!!");
            return;
        }
        
        // TODO check distance
        // Position pos = connection.player.getComponent(Position.class);
        // ===================

        VirtualMonitor virtualMonitor = target.getComponent(VirtualMonitor.class);
        if(virtualMonitor != null) {
            connection.item = virtualMonitor.getHardware(VirtualKeyboard.class);
            Log.info("" + connection.item.toString());
            Mode mode = connection.player.getComponent(Mode.class);
            mode.setMode(CharacterMode.DCPU);
        }
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
            Log.trace("Received: " + o.getClass().getName());
            PlayerConnection pc = (PlayerConnection) connection;

            if(o instanceof Network.Login) {
                handleLoginMessage(pc, (Network.Login) o);

            } else if(o instanceof Network.Logout) {
                handleLogoutMessage(pc, (Network.Logout) o);

            } else if(o instanceof UserCommand) {
                handleUserCmd(pc, (UserCommand) o);
            }
        }
    }
}

