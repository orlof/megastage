package org.megastage.systems.srv;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.dcpu.VirtualKeyboard;
import org.megastage.protocol.Network;
import org.megastage.protocol.PlayerConnection;
import java.io.IOException;
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
import org.megastage.protocol.Message;
import org.megastage.protocol.PlayerIDMessage;
import org.megastage.protocol.UserCommand;
import org.megastage.protocol.UserCommand.Build;
import org.megastage.protocol.UserCommand.Keyboard;
import org.megastage.protocol.UserCommand.Unbuild;
import org.megastage.server.TemplateManager;
import org.megastage.util.Cube3dMap;
import org.megastage.util.Mapper;
import org.megastage.util.Quaternion;
import org.megastage.util.ServerGlobals;
import org.megastage.util.Vector3d;

public class NetworkSystem extends VoidEntitySystem {
    private Server server;
    
    public NetworkSystem() {
    }
    
    @Override
    public void initialize() {
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
        Array<Message> batch = ServerGlobals.getUpdates();
        if(batch != null) {
            // batch.addAll(ServerGlobals.getComponentEvents());
            server.sendToAllUDP(batch.toArray());
        }
    }

    @Override
    protected boolean checkProcessing() {
        return ServerGlobals.updates != null;
    }

    private void handleLogoutMessage(PlayerConnection connection, Network.Logout packet) {
        BindTo bindTo = Mapper.BIND_TO.get(connection.player);
        if(bindTo != null) {
            Entity e = world.getEntity(bindTo.parent);
            if(e != null) e.addComponent(new DeleteFlag());
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
        bind.parent = ship.id;
        connection.player.addComponent(bind);
        connection.player.changedInWorld();

        SpawnPoint sp = Mapper.SPAWN_POINT.get(ship);
        
        Position pos = Mapper.POSITION.get(connection.player);
        pos.set(
                1000 * sp.x + 500,
                1000 * sp.y + 500,
                1000 * sp.z + 500);
        
        connection.sendTCP(new PlayerIDMessage(connection.player.id));
    }

    private void replicateAllEntities(PlayerConnection connection) {
        Array<Entity> entities = world.getManager(GroupManager.class).getEntities("replicate");

        for(Entity entity: entities) {
            replicateComponents(connection, entity);
        }        
    }
    
    private void replicateComponents(PlayerConnection connection, Entity entity) {
        Array<Component> all = new Array<>(20);
        entity.getComponents(all);
        
        Array<Message> list = new Array<>(20);

        for(Component c: all) {
            BaseComponent bc = (BaseComponent) c;
            if(bc.replicate()) {
                Message trans = bc.create(entity);                
                list.add(trans);
            }
        }

        if(list.size > 0) {
            connection.sendTCP(list.toArray());
        }
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
    
    private int collisionXZ(Cube3dMap map, int cx, int cy, int cz, int px, int py, int pz) {
        int result = 0;
        if(cx != px && blocked(map, px, cy, cz)) {
            result |= 1;
        }
        if(cz != pz && blocked(map, cx, cy, pz)) {
            result |= 2;
        }
        if(cx != px && cz != pz && blocked(map, px, cy, pz)) {
            result |= 4;
        }
        return result;
    }
    
    private void handleUserCmd(PlayerConnection connection, UserCommand cmd) {
        if(connection.player == null) return;
        
        if(cmd.build != null || cmd.unbuild != null) Log.info(cmd.toString());
        
        BindTo bindTo = Mapper.BIND_TO.get(connection.player);
        Entity ship = world.getEntity(bindTo.parent);
        ShipGeometry geom = Mapper.SHIP_GEOMETRY.get(ship);

        updatePlayerPosition(geom.map, connection.player, cmd);
        updatePlayerRotation(connection.player, cmd);

        if(cmd.ship != null) {
            updateShip(ship, cmd);
        }

        if(cmd.pick != null) {
            pickItem(connection, cmd);
        }
        
        if(cmd.unpick != null) {
            unpickItem(connection, cmd);
        }

        if(cmd.build != null) {
            build(connection, cmd.build, geom.map);
        }
        
        if(cmd.unbuild != null) {
            unbuild(connection, cmd.unbuild, geom.map);
        }
        
        if(cmd.teleport != null) {
            teleport(connection, cmd.teleport);
        }
        
        Keyboard keys = cmd.keyboard;
        
        if(keys.keyEventPtr > 0 && connection.item != null) {
            VirtualKeyboard kbd = (VirtualKeyboard) connection.item;
            if(!kbd.dcpu.ship.isActive()) {
                unpickItem(connection, cmd);
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
                    }
                }
            }
        }
    }

    private void unpickItem(PlayerConnection connection, UserCommand cmd) {
        connection.item = null;
        Mode mode = Mapper.MODE.get(connection.player);
        mode.setMode(CharacterMode.WALK);
    }
    
    private void pickItem(PlayerConnection connection, UserCommand cmd) {
        Entity target = world.getEntity(cmd.pick.eid);
        if(target == null) {
            return;
        }
        
        // TODO check distance
        // Position pos = connection.player.getComponent(Position.class);
        // ===================

        VirtualMonitor virtualMonitor = Mapper.VIRTUAL_MONITOR.get(target);
        if(virtualMonitor != null) {
            connection.item = virtualMonitor.getHardware(VirtualKeyboard.class);
            Mode mode = Mapper.MODE.get(connection.player);
            mode.setMode(CharacterMode.DCPU);
            return;
        }

        SpawnPoint spawnPoint = Mapper.SPAWN_POINT.get(target);
        if(spawnPoint != null) {
            //TODO
            return;
        }
    }

    private void updatePlayerPosition(Cube3dMap map, Entity player, UserCommand cmd) {
        Position pos = Mapper.POSITION.get(player);
        int cx = block(pos.x);
        int cy = block(pos.y);
        int cz = block(pos.z);
        
        int xprobe = probe(pos.x, (long) (1000.0 * cmd.dx));
        int yprobe = probe(pos.y, (long) (1000.0 * cmd.dy));
        int zprobe = probe(pos.z, (long) (1000.0 * cmd.dz));
        
        int collision = collisionXZ(map, cx, cy, cz, xprobe, yprobe, zprobe);
        if(collision == 0) {
            pos.set(pos.x + (long) (1000 * cmd.dx + 0.5), pos.y, pos.z + (long) (1000 * cmd.dz + 0.5));
        } else if((collision & 1) == 0) {
            pos.set(pos.x + (long) (1000 * cmd.dx + 0.5), pos.y, pos.z);
        } else if((collision & 2) == 0) {
            pos.set(pos.x, pos.y, pos.z + (long) (1000 * cmd.dz + 0.5));
        }
    }

    private void updatePlayerRotation(Entity player, UserCommand cmd) {
        Rotation rot = Mapper.ROTATION.get(player);
        rot.set(cmd.qx, cmd.qy, cmd.qz, cmd.qw);
    }

    private void updateShip(Entity ship, UserCommand cmd) {
        Rotation shipRotation = Mapper.ROTATION.get(ship);
        Quaternion shipRotationQuaternion = shipRotation.getQuaternion4d();

        Vector3d vel = new Vector3d(cmd.ship.left, cmd.ship.up, cmd.ship.forward).multiply(shipRotationQuaternion);

        vel = vel.multiply(10e3);

        Position shipPos = Mapper.POSITION.get(ship);
        shipPos.set(
                shipPos.x + (long) (vel.x + 0.5),
                shipPos.y + (long) (vel.y + 0.5),
                shipPos.z + (long) (vel.z + 0.5));

        // rotate rotation axis by fixedEntity rotation
        Vector3d yAxis = new Vector3d(0, 1, 0).multiply(shipRotationQuaternion);
        Quaternion yRotation = new Quaternion(yAxis, cmd.ship.yaw);

        Vector3d zAxis = new Vector3d(0, 0, 1).multiply(shipRotationQuaternion);
        Quaternion zRotation = new Quaternion(zAxis, cmd.ship.roll);

        Vector3d xAxis = new Vector3d(1, 0, 0).multiply(shipRotationQuaternion);
        Quaternion xRotation = new Quaternion(xAxis, cmd.ship.pitch);

        shipRotationQuaternion = yRotation.multiply(shipRotationQuaternion).normalize();
        shipRotationQuaternion = zRotation.multiply(shipRotationQuaternion).normalize();
        shipRotationQuaternion = xRotation.multiply(shipRotationQuaternion).normalize();

        shipRotation.set(shipRotationQuaternion);
    }

    private void build(PlayerConnection connection, Build build, Cube3dMap map) {
        if(build.x < 0 || build.y < 0 || build.z < 0) {
            return;
        }

        if(map.get(build.x, build.y, build.z) != 0) {
            return;
        }

        map.set(build.x, build.y, build.z, '#');
    }

    private void unbuild(PlayerConnection connection, Unbuild unbuild, Cube3dMap map) {
        if(unbuild.x < 0 || unbuild.y < 0 || unbuild.z < 0) {
            return;
        }

        if(map.get(unbuild.x, unbuild.y, unbuild.z) != '#') {
            return;
        }

        map.set(unbuild.x, unbuild.y, unbuild.z, (char) 0);
    }

    private void teleport(PlayerConnection connection, UserCommand.Teleport teleport) {
        // bind player to ship
        Log.info("");
        BindTo bind = Mapper.BIND_TO.get(connection.player);
        bind.setParent(teleport.eid);

        Entity ship = world.getEntity(teleport.eid);
        SpawnPoint sp = Mapper.SPAWN_POINT.get(ship);
        
        Position pos = Mapper.POSITION.get(connection.player);
        pos.set(
                1000 * sp.x + 500,
                1000 * sp.y + 500,
                1000 * sp.z + 500);
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

