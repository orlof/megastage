package org.megastage.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.VoidEntitySystem;
import org.megastage.util.Globals;
import org.megastage.util.Network;
import org.megastage.components.client.VirtualMonitorView;
import org.megastage.util.NetworkListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class ClientNetworkSystem extends VoidEntitySystem implements NetworkListener {
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
        ByteBuffer buf = ByteBuffer.wrap(new byte[10]);
        buf.putInt(Globals.Message.KEY_TYPED);
        buf.putInt(key);
        network.send(buf);
    }

    public void sendKeyPressed(int key) {
        ByteBuffer buf = ByteBuffer.wrap(new byte[10]);
        buf.putInt(Globals.Message.KEY_PRESSED);
        buf.putInt(key);
        network.send(buf);
    }

    public void sendKeyReleased(int key) {
        ByteBuffer buf = ByteBuffer.wrap(new byte[10]);
        buf.putInt(Globals.Message.KEY_RELEASED);
        buf.putInt(key);
        network.send(buf);
    }

    public void sendUseEntity() {
        System.out.println("ClientNetworkSystem.sendUseEntity");
        ByteBuffer buf = ByteBuffer.wrap(new byte[4]);
        buf.putInt(Globals.Message.USE_ENTITY);
        network.send(buf);
    }

    public void sendRequestEntityData(int entityID) {
        System.out.println("ClientNetworkSystem.sendRequestEntityData");
        ByteBuffer buf = ByteBuffer.wrap(new byte[8]);
        buf.putInt(Globals.Message.REQUEST_ENTITY_DATA);
        buf.putInt(entityID);
        network.send(buf);
    }

    public void sendLogin() {
        System.out.println("ClientNetworkSystem.sendLogin");

        ByteBuffer buf = ByteBuffer.wrap(new byte[10]);
        buf.putInt(Globals.Message.LOGIN);
        network.send(buf);
    }

    public void sendLogout() {
        System.out.println("ClientNetworkSystem.sendLogout");
        ByteBuffer buf = ByteBuffer.wrap(new byte[10]);
        buf.putInt(Globals.Message.LOGOUT);
        network.send(buf);
    }

    HashMap<Integer, Integer> serverToClient = new HashMap<Integer, Integer>();
    HashMap<Integer, Integer> clientToServer = new HashMap<Integer, Integer>();
    
    public void handleMessage(SocketAddress remote, ByteBuffer buf) {
        System.out.println("ClientNetworkSystem.handleMessage");
        int msgID = buf.getInt();

        switch(msgID) {
            case Globals.Message.VIDEO_RAM:
            case Globals.Message.FONT_RAM:
            case Globals.Message.PALETTE_RAM:
                handleRAMMessage(msgID, buf);
                break;
            default:
                System.out.println("ERROR packet");
        }
    }

    @Mapper
    ComponentMapper<VirtualMonitorView> viewMapper;
    private void handleRAMMessage(int msgID, ByteBuffer buf) {
        System.out.println("ClientNetworkSystem.handleVideoRAMMessage");

        int serverID = buf.getInt();
        Entity entity = getEntity(serverID);

        if(entity == null) {
            entity = createEntity(serverID);
            VirtualMonitorView mon = new VirtualMonitorView();
            entity.addComponent(mon);

            sendRequestEntityData(serverID);

            world.getSystem(ClientRenderSystem.class).panel.image = mon.img;
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

    private Entity createEntity(int serverID) {
        Entity entity;
        entity = world.createEntity();
        world.addEntity(entity);
        serverToClient.put(serverID, entity.getId());
        clientToServer.put(entity.getId(), serverID);
        return entity;
    }

    private Entity getEntity(int serverID) {
        Integer clientID = serverToClient.get(serverID);
        return clientID == null ? null: world.getEntity(clientID);
    }

}
