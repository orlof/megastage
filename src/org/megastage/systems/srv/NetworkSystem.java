package org.megastage.systems.srv;

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
import org.megastage.components.DeleteFlag;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.SpawnPoint;
import org.megastage.components.dcpu.VirtualMonitor;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.Mode;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.protocol.Action;
import org.megastage.protocol.CharacterMode;
import org.megastage.protocol.PlayerIDMessage;
import org.megastage.protocol.UserCommand;
import org.megastage.server.TemplateManager;
import org.megastage.util.Cube3dMap;
import org.megastage.util.Quaternion;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Vector;

public class NetworkSystem extends VoidEntitySystem {
    private Server server;
    
    public NetworkSystem() {
    }
    
    @Override
    protected void initialize() {
        server = new Server(16*1024, 16*1024) {
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
        Log.trace("Client state refreshed with packet size " + ServerGlobals.updates.size());
        Bag batch = ServerGlobals.getUpdates();
        // batch.addAll(ServerGlobals.getComponentEvents());
        server.sendToAllUDP(batch);
    }

    @Override
    protected boolean checkProcessing() {
        return ServerGlobals.updates != null;
    }

    private void handleLogoutMessage(PlayerConnection connection, Network.Logout packet) {
        BindTo bindTo = connection.player.getComponent(BindTo.class);
        if(bindTo != null) {
            Entity e = world.getEntity(bindTo.parent);
            e.addComponent(new DeleteFlag());
        }
        
        connection.player.addComponent(new DeleteFlag());
        connection.close();
    }

    private void handleLoginMessage(PlayerConnection connection, Network.Login packet) {
        // replicate
        replicateAllEntities(connection);

        // create ship
        Entity ship = world.getManager(TemplateManager.class).create("Apollo 13");
        
        // create character
        connection.player = world.getManager(TemplateManager.class).create("Player");

        // bind player to ship
        BindTo bind = new BindTo();
        bind.parent = ship.getId();
        connection.player.addComponent(bind);
        connection.player.changedInWorld();

        SpawnPoint sp = ship.getComponent(SpawnPoint.class);
        
        Position pos = connection.player.getComponent(Position.class);
        pos.x = 1000 * sp.x;
        pos.y = 1000 * sp.y;
        pos.z = 1000 * sp.z;
        
        connection.sendTCP(new PlayerIDMessage(connection.player.getId()));
        Log.info("Sent player entity id: " + connection.player.toString());
    }

    private void replicateAllEntities(PlayerConnection connection) {
        ImmutableBag<Entity> entities = world.getManager(GroupManager.class).getEntities("replicate");
        Log.info("Replicate " + entities.size() + " entities for " + connection.toString());

        for(int i=0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            Log.info("Replicate #" + i + " entity " + entity.toString() + " for " + connection.toString());
            replicateComponents(connection, entity);
        }        
    }
    
    private void replicateComponents(PlayerConnection connection, Entity entity) {
        Bag<Component> components = entity.getComponents(new Bag<Component>());
        ArrayList list = new ArrayList();

        for(int j=0; j < components.size(); j++) {
            BaseComponent comp = (BaseComponent) components.get(j);
            if(comp.replicate()) {
                Object trans = comp.create(entity);                
                list.add(trans);
                Log.info("Replicate " + comp.toString() + " -> " + trans.toString() + " for " + connection.toString());
            }
        }

        if(!list.isEmpty()) {
            connection.sendTCP(list.toArray());
        }

        Log.info("Sent " + list.size() + " components");
    }

    private int probe(long pos, long step) {
        long target = pos + step;
        if(step < 0) {
            target -= 300;
        } else if(step > 0) {
            target += 300;
        }

        if(target < 0) {
            target -= 1000;
        }

        target /= 1000;
        return (int) target;
    }
    
    private int block(long pos) {
        if(pos < 0) {
            pos -= 1000;
        }

        pos /= 1000;
        return (int) pos;
    }
    
    private boolean blocked(Cube3dMap map, int x, int y, int z) {
        return map.get(x, y, z) != 0 || map.get(x, y+1, z) != 0 || 
                map.get(x, y-1, z) != '#';
    }
    
    private boolean detectCollision(Cube3dMap map, int cx, int cy, int cz, int px, int py, int pz) {
        if(cx != px && blocked(map, px, cy, cz)) {
            return true;
        }
        if(cz != pz && blocked(map, cx, cy, pz)) {
            return true;
        }
        if(cx != px && cz != pz && blocked(map, px, cy, pz)) {
            return true;
        }
        return false;
    }
    
    private void handleUserCmd(PlayerConnection connection, UserCommand cmd) {
        if(connection.player == null) return;
        
        Log.debug(cmd.toString());

        BindTo bindTo = connection.player.getComponent(BindTo.class);
        Entity ship = world.getEntity(bindTo.parent);

        ShipGeometry geom = ship.getComponent(ShipGeometry.class);
        
        Position pos = connection.player.getComponent(Position.class);
        int cx = block(pos.x);
        int cy = block(pos.y);
        int cz = block(pos.z);
        
        int xprobe = probe(pos.x, (long) (1000.0 * cmd.xMove));
        int yprobe = probe(pos.y, (long) (1000.0 * cmd.yMove));
        int zprobe = probe(pos.z, (long) (1000.0 * cmd.zMove));
        
        if(Log.DEBUG && (cmd.xMove != 0 || cmd.zMove != 0)) {
            Log.info("" + pos.x + ", " + pos.y + ", " + pos.z);
        }

        if(!detectCollision(geom.map, cx, cy, cz, xprobe, yprobe, zprobe)) {
            pos.x += 1000 * cmd.xMove;
            pos.y += 1000 * cmd.yMove;
            pos.z += 1000 * cmd.zMove;
        }

        Rotation rot = connection.player.getComponent(Rotation.class);
        rot.x = cmd.qx;
        rot.y = cmd.qy;
        rot.z = cmd.qz;
        rot.w = cmd.qw;
        
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

