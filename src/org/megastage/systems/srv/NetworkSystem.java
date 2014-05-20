package org.megastage.systems.srv;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.dcpu.VirtualKeyboard;
import org.megastage.protocol.Network;
import org.megastage.protocol.PlayerConnection;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.megastage.components.BaseComponent;
import org.megastage.components.DeleteFlag;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.SpawnPoint;
import org.megastage.components.dcpu.VirtualMonitor;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.Mode;
import org.megastage.components.dcpu.DCPU;
import org.megastage.components.dcpu.DCPUHardware;
import org.megastage.components.dcpu.VirtualFloppyDrive;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.protocol.CharacterMode;
import org.megastage.protocol.Message;
import org.megastage.protocol.Network.TimestampMessage;
import org.megastage.protocol.PlayerIDMessage;
import org.megastage.protocol.UserCommand;
import org.megastage.protocol.UserCommand.Build;
import org.megastage.protocol.UserCommand.ChangeBootRom;
import org.megastage.protocol.UserCommand.ChangeFloppy;
import org.megastage.protocol.UserCommand.Keyboard;
import org.megastage.protocol.UserCommand.Unbuild;
import org.megastage.server.TemplateManager;
import org.megastage.util.Bag;
import org.megastage.util.Cube3dMap;
import org.megastage.util.Cube3dMap.BlockChange;
import org.megastage.util.ID;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector3d;

public class NetworkSystem extends Processor {
    private Server server;
    public static Bag<Message> updates;
    
    public NetworkSystem(World world, long interval) {
        super(world, interval, CompType.ReplicateToNewConnectionsFlag);
        getUpdates();
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

        Network.register(server.getKryo());

        new Thread(server).start();

        server.addListener(new ServerNetworkListener());

        try {
            server.bind(Network.serverPort, Network.serverPort + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void process() {
        Connection[] connections = server.getConnections();

        for(Connection c: connections) {
            PlayerConnection pc = (PlayerConnection) c;
            if(!pc.isInitialized) {
                pc.isInitialized = true;
                replicateAllEntities(pc);
                initNewPlayer(pc);
            }
        }
        
        Message[] data = getUpdates().toArray(Message.class);
        ((TimestampMessage) data[0]).time = world.time;

        for(Connection c: connections) {
            c.sendUDP(data);
        }
    }


    
    @Override
    protected boolean checkProcessing() {
        return !updates.isEmpty();
    }

    private static Bag<Message> getUpdates() {
        Bag<Message> old = updates;
        updates = new Bag<>(100);
        updates.add(new TimestampMessage());
        return old;
    }

    private void handleLogoutMessage(PlayerConnection connection, Network.Logout packet) {
        BindTo bindTo = (BindTo) world.getComponent(connection.player, CompType.BindTo);
        if(bindTo != null) {
            if(bindTo.parent != 0) world.addComponent(bindTo.parent, CompType.BindTo, new DeleteFlag());
        }
        
        world.addComponent(connection.player, CompType.DeleteFlag, new DeleteFlag());
        connection.close();
        
        if(server.getConnections().length == 0) {
            server.stop();
            System.exit(0);
        }
    }

    private void initNewPlayer(PlayerConnection connection) {
        try {
            // create ship
            int ship = TemplateManager.create(world, "Apollo 13");
            
            // create character
            connection.player = TemplateManager.create(world, "Player");

            // bind player to ship
            BindTo bind = new BindTo();
            bind.parent = ship;
            world.addComponent(connection.player, CompType.BindTo, bind);
            
            SpawnPoint sp = (SpawnPoint) world.getComponent(ship, CompType.SpawnPoint);
            
            Position pos = (Position) world.getComponent(connection.player, CompType.Position);
            pos.set(
                    1000 * sp.x + 500,
                    1000 * sp.y + 500,
                    1000 * sp.z + 500);
            
            connection.sendTCP(new PlayerIDMessage(connection.player));
        } catch (Exception ex) {
            Logger.getLogger(NetworkSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void handleLoginMessage(PlayerConnection connection, Network.Login packet) throws Exception {
        
    }

    private void replicateAllEntities(PlayerConnection connection) {
        for(int eid = group.iterator(); eid != 0; eid = group.next()) {
            Log.info(ID.get(eid));
            replicateComponents(connection, eid);
        }        
    }
    
    private void replicateComponents(PlayerConnection connection, int eid) {
        Bag<Message> list = new Bag<>(20);

        for(Object comp=world.compIter(eid); comp != null; comp=world.compNext()) {
            Message msg = ((BaseComponent) comp).replicate(eid);
            if(msg != null) {
                list.add(msg);
            }
        }

        if(list.size() > 0) {
            connection.sendTCP(list.toArray(Message.class));
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
        // TODO check player mode
        if(connection.player == 0) return;
        
        if(cmd.build != null || cmd.unbuild != null) Log.info(cmd.toString());
        
        BindTo bindTo = (BindTo) world.getComponent(connection.player, CompType.BindTo);
        if(bindTo.parent == 0) return;

        ShipGeometry geom = (ShipGeometry) world.getComponent(bindTo.parent, CompType.ShipGeometry);

        updatePlayerPosition(geom.map, connection.player, cmd);
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
            build(connection, cmd.build, geom.map);
        }
        
        if(cmd.unbuild != null) {
            unbuild(connection, cmd.unbuild, geom.map);
        }
        
        if(cmd.teleport != null) {
            teleport(connection, cmd.teleport);
        }
        
        if(cmd.floppy != null) {
            changeFloppy(connection, cmd.floppy);
        }
        
        if(cmd.bootRom != null) {
            changeBootRom(connection, cmd.bootRom);
        }
        
        Keyboard keys = cmd.keyboard;
        
        if(keys.keyEventPtr > 0 && connection.item >= 0) {
            VirtualKeyboard kbd = (VirtualKeyboard) world.getComponent(connection.item, CompType.VirtualKeyboard);

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

    private void unpickItem(PlayerConnection connection, UserCommand cmd) {
        connection.item = -1;
        Mode mode = (Mode) world.getComponent(connection.player, CompType.Mode);
        mode.setMode(CharacterMode.WALK);
    }
    
    private void pickItem(PlayerConnection connection, UserCommand cmd) {
        int target = cmd.pick.eid;
        if(!world.hasEntity(target)) {
            Log.error("No such item to pick: " + target);
            return;
        }
        
        // TODO check distance
        // Position pos = connection.player.getComponent(Position.class);
        // ===================

        VirtualMonitor mon = (VirtualMonitor) world.getComponent(target, CompType.VirtualMonitor);
        if(mon != null) {
            DCPU dcpu = (DCPU) world.getComponent(mon.dcpuEID, CompType.DCPU);

            for(int i=0; i < dcpu.hardwareSize; i++) {
                VirtualKeyboard kbd = (VirtualKeyboard) world.getComponent(dcpu.hardware[i], CompType.VirtualKeyboard);
                if(kbd != null) {
                    connection.item = dcpu.hardware[i];
                    Mode mode = (Mode) world.getComponent(connection.player, CompType.Mode);
                    mode.setMode(CharacterMode.DCPU);
                    return;
                }
            }
            return;
        }

        VirtualFloppyDrive fd = (VirtualFloppyDrive) world.getComponent(target, CompType.VirtualFloppyDrive);
        if(fd != null) {
            connection.item = target;
            Mode mode = (Mode) world.getComponent(connection.player, CompType.Mode);
            mode.setMode(CharacterMode.MENU);
            return;
        }
    }

    private void updatePlayerPosition(Cube3dMap map, int player, UserCommand cmd) {
        Position pos = (Position) world.getComponent(player, CompType.Position);
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

    private void updatePlayerRotation(int player, UserCommand cmd) {
        Rotation rot = (Rotation) world.getComponent(player, CompType.Rotation);

        rot.set(cmd.qx, cmd.qy, cmd.qz, cmd.qw);
    }

    private void updateShip(int ship, UserCommand cmd) {
        Rotation shipRotation = (Rotation) world.getComponent(ship, CompType.Rotation);
        Quaternion shipRotationQuaternion = shipRotation.getQuaternion4d();

        Vector3d vel = new Vector3d(cmd.ship.left, cmd.ship.up, cmd.ship.forward).multiply(shipRotationQuaternion);

        vel = vel.multiply(10e3);

        Position shipPos = (Position) world.getComponent(ship, CompType.Position);
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

    private void changeFloppy(PlayerConnection connection, ChangeFloppy change) {
        VirtualFloppyDrive vfd = (VirtualFloppyDrive) world.getComponent(connection.item, CompType.VirtualFloppyDrive);
        if(vfd != null) {
            DCPU dcpu = (DCPU) world.getComponent(vfd.dcpuEID, CompType.DCPU);
            vfd.eject(dcpu);
            vfd.insert(dcpu, change.filename);
            unpickItem(connection, null);
        }
    }

    private void changeBootRom(PlayerConnection connection, ChangeBootRom change) {
        VirtualFloppyDrive vfd = (VirtualFloppyDrive) world.getComponent(connection.item, CompType.VirtualFloppyDrive);
        if(vfd != null) {
            DCPU dcpu = (DCPU) world.getComponent(vfd.dcpuEID, CompType.DCPU);
            dcpu.reset(change.filename);
            unpickItem(connection, null);
        }
    }

    private void build(PlayerConnection connection, Build build, Cube3dMap map) {
        if(build.x < 0 || build.y < 0 || build.z < 0) {
            return;
        }

        if(map.get(build.x, build.y, build.z) != 0) {
            return;
        }

        map.set(build.x, build.y, build.z, '#', BlockChange.BUILD);
    }

    private void unbuild(PlayerConnection connection, Unbuild unbuild, Cube3dMap map) {
        if(unbuild.x < 0 || unbuild.y < 0 || unbuild.z < 0) {
            return;
        }

        if(map.get(unbuild.x, unbuild.y, unbuild.z) != '#') {
            return;
        }

        map.set(unbuild.x, unbuild.y, unbuild.z, (char) 0, BlockChange.UNBUILD);
    }

    private void teleport(PlayerConnection connection, UserCommand.Teleport teleport) {
        // bind player to ship
        Log.info("");
        BindTo bind = (BindTo) world.getComponent(connection.player, CompType.BindTo);
        bind.setParent(teleport.eid);

        SpawnPoint sp = (SpawnPoint) world.getComponent(teleport.eid, CompType.SpawnPoint);
        
        Position pos = (Position) world.getComponent(connection.player, CompType.Position);
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
            try {
                PlayerConnection pc = (PlayerConnection) connection;

                if(o instanceof Network.Login) {
                    handleLoginMessage(pc, (Network.Login) o);

                } else if(o instanceof Network.Logout) {
                    handleLogoutMessage(pc, (Network.Logout) o);

                } else if(o instanceof UserCommand) {
                    handleUserCmd(pc, (UserCommand) o);
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
