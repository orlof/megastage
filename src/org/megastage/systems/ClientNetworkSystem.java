package org.megastage.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.VoidEntitySystem;
import org.megastage.util.Globals;
import org.megastage.util.Network;
import org.megastage.components.client.VirtualMonitorView;
import org.megastage.util.NetworkListener;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.logging.Logger;

public class ClientNetworkSystem extends VoidEntitySystem implements NetworkListener {
    private final static Logger LOG = Logger.getLogger(ClientNetworkSystem.class.getName());

    private Network network;

    public ClientNetworkSystem() {
        super();

        network = new Network(this, Globals.clientPort);
        // add server as the only remote
        network.addRemote(new InetSocketAddress(Globals.serverHost, Globals.serverPort));
    }

    @Override
    protected void processSystem() {
        network.tick();
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    public void sendKeyTyped(int key) {
        LOG.fine("");
        ByteBuffer buf = ByteBuffer.wrap(new byte[10]);
        buf.putInt(Globals.Message.KEY_TYPED);
        buf.putInt(key);
        network.broadcast(buf);
    }

    public void sendKeyPressed(int key) {
        LOG.fine("");
        ByteBuffer buf = ByteBuffer.wrap(new byte[10]);
        buf.putInt(Globals.Message.KEY_PRESSED);
        buf.putInt(key);
        network.broadcast(buf);
    }

    public void sendKeyReleased(int key) {
        LOG.fine("");
        ByteBuffer buf = ByteBuffer.wrap(new byte[10]);
        buf.putInt(Globals.Message.KEY_RELEASED);
        buf.putInt(key);
        network.broadcast(buf);
    }

    public void sendUseEntity() {
        LOG.fine("");
        ByteBuffer buf = ByteBuffer.wrap(new byte[4]);
        buf.putInt(Globals.Message.USE_ENTITY);
        network.broadcast(buf);
    }

    public void sendRequestEntityData(int entityID) {
        LOG.fine("");
        ByteBuffer buf = ByteBuffer.wrap(new byte[8]);
        buf.putInt(Globals.Message.REQUEST_ENTITY_DATA);
        buf.putInt(entityID);
        network.broadcast(buf);
    }

    public void sendLogin() {
        LOG.info("");

        ByteBuffer buf = ByteBuffer.wrap(new byte[10]);
        buf.putInt(Globals.Message.LOGIN);
        network.broadcast(buf);
    }

    public void sendLogout() {
        LOG.fine("");
        ByteBuffer buf = ByteBuffer.wrap(new byte[10]);
        buf.putInt(Globals.Message.LOGOUT);
        network.broadcast(buf);
    }

    HashMap<Integer, Entity> serverIDToClientEntity = new HashMap<Integer, Entity>();
    HashMap<Integer, Integer> clientToServer = new HashMap<Integer, Integer>();
    
    public void handleMessage(SocketAddress remote, ByteBuffer buf) {
        int msgID = buf.getInt();

        switch(msgID) {
            case Globals.Message.VIDEO_RAM:
            case Globals.Message.FONT_RAM:
            case Globals.Message.PALETTE_RAM:
                handleRAMMessage(msgID, buf);
                break;
            default:
                LOG.warning("Unknown message type: " + msgID);
        }
    }

    @Mapper
    ComponentMapper<VirtualMonitorView> viewMapper;
    private void handleRAMMessage(int msgID, ByteBuffer buf) {
        LOG.fine("" + msgID);

        int serverID = buf.getInt();

        Entity entity = serverIDToClientEntity.get(serverID);

        if(entity == null) {
            entity = world.createEntity();

            VirtualMonitorView mon = new VirtualMonitorView();
            entity.addComponent(mon);

            // sendRequestEntityData(serverID);

            world.getSystem(ClientRenderSystem.class).panel.image = mon.img;

            world.addEntity(entity);
            serverIDToClientEntity.put(serverID, entity);
            clientToServer.put(entity.getId(), serverID);
        }

        VirtualMonitorView monitor = viewMapper.get(entity);
        
        char[] mem = new char[buf.remaining()/2];
        for(int i=0; i < mem.length; i++) {
            mem[i] = buf.getChar();
        }

        switch(msgID) {
            case Globals.Message.VIDEO_RAM:
                monitor.updateVideo(mem);
                break;
            case Globals.Message.FONT_RAM:
                monitor.updateFont(mem);
                break;
            case Globals.Message.PALETTE_RAM:
                monitor.updatePalette(mem);
                break;
        }
    }
}
