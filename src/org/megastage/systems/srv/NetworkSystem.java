package org.megastage.systems.srv;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.megastage.util.Log;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.megastage.components.dcpu.VirtualKeyboard;
import org.megastage.protocol.Network;
import org.megastage.protocol.PlayerConnection;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.megastage.components.BlockChange;
import org.megastage.components.DeleteFlag;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.srv.SpawnPoint;
import org.megastage.components.dcpu.VirtualMonitor;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.Mode;
import org.megastage.components.dcpu.DCPU;
import org.megastage.components.dcpu.VirtualFloppyDrive;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.components.srv.BlockChanges;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Group;
import org.megastage.ecs.Processor;
import org.megastage.ecs.ReplicatedComponent;
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
import org.megastage.server.ServerGlobals;
import org.megastage.server.TemplateManager;
import org.megastage.util.Bag;
import org.megastage.util.Cube3dMap;
import org.megastage.util.ID;

public class NetworkSystem extends Processor {
    private Server server;
    private Group deleted;
    
    public NetworkSystem(World world, long interval) {
        super(world, interval, CompType.SynchronizeFlag);
        deleted = world.createGroup(CompType.DeleteFlag);
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
        // Log.info(getClass().getSimpleName());
        Connection[] connections = server.getConnections();
        processNewConnections(connections);
        
        Bag<Message> update = new Bag<>(100);
        update.add(new TimestampMessage());

        processDeletedEntities(update);
        processSynchronizedEntities(update);        

        if(update.size() > 1) {
            Message[] data = update.toArray(Message.class);

            for(Connection c: connections) {
                //Log.info("sending %d messages", data.length);
                c.sendUDP(data);
            }
        }
    }

    private void handleLogoutMessage(PlayerConnection connection, Network.Logout packet) {
        world.setComponent(connection.player, CompType.DeleteFlag, new DeleteFlag());
        connection.close();
        
        if(ServerGlobals.autoexit && server.getConnections().length == 0) {
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
            world.setComponent(connection.player, CompType.BindTo, bind);
            
            SpawnPoint sp = (SpawnPoint) world.getComponent(ship, CompType.SpawnPoint);
            
            Position pos = (Position) world.getComponent(connection.player, CompType.Position);
            pos.set(sp.vector);
            
            Log.info(sp.toString());
            
            connection.sendTCP(new PlayerIDMessage(connection.player));
        } catch (Exception ex) {
            Logger.getLogger(NetworkSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void handleLoginMessage(PlayerConnection connection, Network.Login packet) throws Exception {
    }

    private void replicateEntitiesToNewConnection(PlayerConnection connection) {
        for(int eid = group.iterator(); eid != 0; eid = group.next()) {
            Log.info(ID.get(eid));
            replicateComponents(connection, eid);
        }        
    }
    
    private void replicateComponents(PlayerConnection connection, int eid) {
        Bag<Message> list = new Bag<>(20);

        for(ReplicatedComponent comp = world.compIter(eid, ReplicatedComponent.class); comp != null; comp=world.compNext()) {
            if(comp.isReplicable()) {
                Log.info(comp.toString());
                Message msg = comp.synchronize(eid);
                list.add(msg);
            }
        }

        if(list.size() > 0) {
            connection.sendTCP(list.toArray(Message.class));
        }
    }

    private int probe(float pos, float step) {
        float target = pos + step + Math.signum(step) * 0.3f;
        if(target < 0.0f) target -= 1.0f;
        
        int start = (int) pos;
        int end = (int) target;
        
        if(end < start - 1) end = start - 1;
        if(end > start + 1) end = start + 1;

        return end;
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
            result |= 3;
        }
        return result;
    }
    
    private void handleUserCmd(PlayerConnection connection, UserCommand cmd) {
        // TODO check player mode
        if(connection.player == 0) return;
        
        BindTo bindTo = (BindTo) world.getComponent(connection.player, CompType.BindTo);
        if(bindTo.parent == 0) return;

        ShipGeometry geom = (ShipGeometry) world.getComponent(bindTo.parent, CompType.ShipGeometry);

        if(cmd.move.lengthSquared() > 0) {
            updatePlayerPosition(geom.map, connection.player, cmd);
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
            build(connection, cmd.build, geom.map, changes);
        }
        
        if(cmd.unbuild != null) {
            BlockChanges changes = (BlockChanges) world.getComponent(bindTo.parent, CompType.BlockChanges);
            unbuild(connection, cmd.unbuild, geom.map, changes);
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
        
        Vector3f coord = pos.get();
        int cx = (int) coord.x;
        int cy = (int) coord.y;
        int cz = (int) coord.z;
        
        int xprobe = probe(coord.x, cmd.move.x);
        int yprobe = probe(coord.y, cmd.move.y);
        int zprobe = probe(coord.z, cmd.move.z);
        
        int collision = collisionXZ(map, cx, cy, cz, xprobe, yprobe, zprobe);
        if((collision & 1) != 0) {
            cmd.move.setX(0.0f);
        } 
        if((collision & 2) != 0) {
            cmd.move.setZ(0.0f);
        }
        pos.move(cmd.move);
    }

    private void updatePlayerRotation(int player, UserCommand cmd) {
        Rotation rot = (Rotation) world.getComponent(player, CompType.Rotation);
        rot.set(cmd.rot);
    }

    private void updateShip(int ship, UserCommand cmd) {
        Rotation shipRotation = (Rotation) world.getComponent(ship, CompType.Rotation);
        Quaternion shipRotationQuaternion = shipRotation.get();

        Vector3f vel = shipRotation.rotateLocal(new Vector3f(cmd.ship.left, cmd.ship.up, cmd.ship.forward));

        vel.multLocal(4.0f);

        Position shipPos = (Position) world.getComponent(ship, CompType.Position);
        shipPos.move(vel);

        // rotate rotation axis by fixedEntity rotation
        // this code is crazy - does it even work?
        // luckily this is only for debugging
        Vector3f yAxis = shipRotationQuaternion.multLocal(new Vector3f(0, 1, 0));
        Quaternion yRotation = new Quaternion().fromAngleAxis(cmd.ship.yaw, yAxis);

        Vector3f zAxis = shipRotationQuaternion.multLocal(new Vector3f(0, 0, 1));
        Quaternion zRotation = new Quaternion().fromAngleAxis(cmd.ship.roll, zAxis);

        Vector3f xAxis = shipRotationQuaternion.multLocal(new Vector3f(1, 0, 0));
        Quaternion xRotation = new Quaternion().fromAngleAxis(cmd.ship.pitch, xAxis);

        shipRotationQuaternion = yRotation.multLocal(shipRotationQuaternion).normalizeLocal();
        shipRotationQuaternion = zRotation.multLocal(shipRotationQuaternion).normalizeLocal();
        shipRotationQuaternion = xRotation.multLocal(shipRotationQuaternion).normalizeLocal();

        shipRotation.set(shipRotationQuaternion);
        shipRotation.setDirty(true);
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

    private void build(PlayerConnection connection, Build build, Cube3dMap map, BlockChanges changes) {
        if(build.x < 0 || build.y < 0 || build.z < 0) {
            Log.info("Trying to build to negative coordinates");
            return;
        }

        if(map.get(build.x, build.y, build.z) != 0) {
            Log.info("Trying to build in non-empty block");
            return;
        }
    
        if(!isBlockDistanceBetween(connection.player, build.x, build.y, build.z, 1.1f, 3.0f)) {
            Log.info("Trying to build too near or far");
            return;
        }

        map.set(build.x, build.y, build.z, '#');
        changes.changes.add(new BlockChange(build.x, build.y, build.z, '#', BlockChange.BUILD));
    }

    private void unbuild(PlayerConnection connection, Unbuild unbuild, Cube3dMap map, BlockChanges changes) {
        if(unbuild.x < 0 || unbuild.y < 0 || unbuild.z < 0) {
            return;
        }

        if(map.get(unbuild.x, unbuild.y, unbuild.z) != '#') {
            return;
        }

        if(!isBlockDistanceBetween(connection.player, unbuild.x, unbuild.y, unbuild.z, 1.1f, 3.0f)) {
            return;
        }

        map.set(unbuild.x, unbuild.y, unbuild.z, (char) 0);
        changes.changes.add(new BlockChange(unbuild.x, unbuild.y, unbuild.z, (char) 0, BlockChange.UNBUILD));
    }

    private void teleport(PlayerConnection connection, UserCommand.Teleport teleport) {
        // bind player to ship
        Log.info("");
        BindTo bind = (BindTo) world.getComponent(connection.player, CompType.BindTo);
        bind.setParent(teleport.eid);

        SpawnPoint sp = (SpawnPoint) world.getComponent(teleport.eid, CompType.SpawnPoint);
        
        Position pos = (Position) world.getComponent(connection.player, CompType.Position);
        pos.set(sp.vector);
    }

    public void processNewConnections(Connection[] connections) {
        for(Connection c: connections) {
            PlayerConnection pc = (PlayerConnection) c;
            if(!pc.isInitialized) {
                pc.isInitialized = true;
                replicateEntitiesToNewConnection(pc);
                initNewPlayer(pc);
            }
        }
    }

    public void processDeletedEntities(Bag<Message> update) {
        for (int eid = deleted.iterator(); eid != 0; eid = deleted.next()) {
            Log.info(ID.get(eid));
            DeleteFlag df = (DeleteFlag) world.getComponent(eid, CompType.DeleteFlag);
            update.add(new Network.ComponentMessage(eid, df));
            world.deleteEntity(eid);
       }
    }

    public void processSynchronizedEntities(Bag<Message> update) {
        for (int eid = group.iterator(); eid != 0; eid = group.next()) {
            for(ReplicatedComponent comp = world.compIter(eid, ReplicatedComponent.class); comp != null; comp = world.compNext()) {
                if(comp.isDirty()) {
                    // Log.info(format("[%d] %s", eid, comp));
                    comp.setDirty(false);
                    Message msg = comp.synchronize(eid);
                    update.add(msg);
                }
            }
        }
    }

    private boolean isBlockDistanceBetween(int eid, int x, int y, int z, float min, float max) {
        Position pos = (Position) World.INSTANCE.getComponent(eid, CompType.Position);
        Vector3f buildPosition = new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
        
        Log.info("Builder position: " + pos.toString());
        Log.info("Build position: " + buildPosition.toString());
        
        float distance = pos.get().distance(buildPosition);
        
        return distance > min && distance < max;
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
            // Log.info("Received: " + o.getClass().getName());
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
