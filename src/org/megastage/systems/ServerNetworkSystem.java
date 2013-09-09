package org.megastage.systems;

import com.artemis.*;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import org.megastage.components.dcpu.VirtualMonitor;
import org.megastage.util.Globals;
import org.megastage.util.Network;
import org.megastage.components.dcpu.VirtualKeyboard;
import org.megastage.util.NetworkListener;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class ServerNetworkSystem extends VoidEntitySystem implements NetworkListener {
    @Mapper ComponentMapper<VirtualKeyboard> virtualKeyboardMapper;

    private Network network;

    public ServerNetworkSystem() {
        super();
        network = new Network(this, Globals.serverPort);
    }

    @Override
    protected void processSystem() {
        network.tick();        
    }

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    public void sendMemory(int messageID, Entity entity, char[] data) {
        System.out.println("ServerNetworkSystem.sendMemory");

        ByteBuffer buffer = ByteBuffer.wrap(new byte[2*data.length + 8]); // 2*384 + 4 + 4 = 776
        buffer.putInt(messageID);
        buffer.putInt(entity.getId());

        for(char c: data) {
            buffer.putChar(c);
        }

        network.send(buffer);
    }

    public void sendMemory(SocketAddress remote, int messageID, Entity entity, char[] data) {
        System.out.println("ServerNetworkSystem.sendMemory");

        ByteBuffer buffer = ByteBuffer.wrap(new byte[2*data.length + 8]); // 2*384 + 4 + 4 = 776
        buffer.putInt(messageID);
        buffer.putInt(entity.getId());

        for(char c: data) {
            buffer.putChar(c);
        }

        network.send(remote, buffer);
    }

    private void sendStartUse(Entity entity) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[10]);
        buffer.putInt(Globals.Message.START_USE);
        buffer.putInt(entity.getId());

        network.send(buffer);
    }

    public void handleMessage(SocketAddress remote, ByteBuffer buf) {
        System.out.println("ServerNetworkSystem.handleMessage received " + buf.remaining() + " bytes from " + remote.toString());
        int msgID = buf.getInt();

        switch(msgID) {
            case Globals.Message.LOGIN:
                handleLoginMessage(remote);
                break;
            case Globals.Message.LOGOUT:
                handleLogoutMessage(remote);
                break;
            case Globals.Message.USE_ENTITY:
                handleUseEntityMessage(remote);
                break;
            case Globals.Message.REQUEST_ENTITY_DATA:
                handleRequestEntityDataMessage(remote, buf.getInt());
                break;
            case Globals.Message.KEY_TYPED:
            case Globals.Message.KEY_PRESSED:
            case Globals.Message.KEY_RELEASED:
                handleKeyMessage(remote, buf.getInt(), msgID);
                break;
            default:
                System.out.println("ERROR packet");
        }
    }

    private void handleKeyMessage(SocketAddress remote, int key, int messageType) {
        System.out.println("ServerNetworkSystem.handleKeyTypedMessage");
        String tag = Globals.Tag.IN_USE + remote.toString();
        Entity entity = world.getManager(TagManager.class).getEntity(tag);

        if(entity != null) {
            VirtualKeyboard kbd = virtualKeyboardMapper.get(entity);
            switch(messageType) {
                case Globals.Message.KEY_TYPED:
                    kbd.keyTyped(key);
                    break;
                case Globals.Message.KEY_PRESSED:
                    kbd.keyPressed(key);
                    break;
                case Globals.Message.KEY_RELEASED:
                    kbd.keyReleased(key);
                    break;
            }
        }
    }

    private void handleUseEntityMessage(SocketAddress remote) {
        System.out.println("ServerNetworkSystem.handleUseEntityMessage");

        GroupManager groupManager = world.getManager(GroupManager.class);
        ImmutableBag<Entity> groupCanUse = groupManager.getEntities(Globals.Group.CAN_USE);

        // TODO replace with position based search
        Entity entity = groupCanUse.get(0);

        String tag = Globals.Tag.IN_USE + remote.toString();

        world.getManager(TagManager.class).register(tag, entity);
        
        sendEntityDataMessage(remote, entity.getId());
    }

    private void handleRequestEntityDataMessage(SocketAddress remote, int entityID) {
        System.out.println("ServerNetworkSystem.handleRequestEntityDataMessage");

        sendEntityDataMessage(remote, entityID);
                
    }

    private void sendEntityDataMessage(SocketAddress remote, int entityID) {
        Entity entity = world.getEntity(entityID);
        Bag<Component> bag = entity.getComponents(new Bag<Component>());
        for(int i=0; i < bag.size(); i++) {
            Component component = bag.get(i);
            if(component instanceof VirtualMonitor) {
                VirtualMonitor mon = (VirtualMonitor) component;
                sendMemory(remote, Globals.Message.VIDEO_RAM, entity, mon.videoRAM.mem);
                sendMemory(remote, Globals.Message.FONT_RAM, entity, mon.fontRAM.mem);
                sendMemory(remote, Globals.Message.PALETTE_RAM, entity, mon.paletteRAM.mem);
            }
        }
    }

    private void handleLoginMessage(SocketAddress c) {
        System.out.println("ServerNetworkSystem.login: " + c.toString());
        network.addRemote(c);
    }

    private void handleLogoutMessage(SocketAddress c) {
        System.out.println("ServerNetworkSystem.logout: " + c.toString());
        network.removeRemote(c);
    }
}
