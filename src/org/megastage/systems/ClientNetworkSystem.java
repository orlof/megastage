package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import org.megastage.protocol.Network;

import java.io.IOException;
import java.util.concurrent.Callable;
import org.megastage.protocol.Message;
import org.megastage.util.ClientGlobals;

public class ClientNetworkSystem extends EntitySystem {
    private Client client;

    public ClientNetworkSystem(long interval) {
        super(Aspect.getEmpty());
        this.interval = interval;
    }

    private long interval;
    private long acc;
    
    @Override
    protected boolean checkProcessing() {
        if(ClientGlobals.time >= acc) {
                acc = ClientGlobals.time + interval;
                return true;
        }
        return false;
    }

    @Override
    protected void initialize() {
        client = new Client(8192,8192);
        Network.register(client);

        Thread kryoThread = new Thread(client);
        kryoThread.setDaemon(true);
        kryoThread.start();

        try {
            client.connect(5000, ClientGlobals.serverHost, Network.serverPort, Network.serverPort+1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.addListener(new ClientNetworkListener());        
        client.updateReturnTripTime();
        while(client.getReturnTripTime() == -1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
        Log.info("RTT: "+ client.getReturnTripTime());
        ClientGlobals.timeDiff -= client.getReturnTripTime();
    }

    @Override
    protected final void processEntities(ImmutableBag<Entity> entities) {
        processSystem();
    }
	
    protected void processSystem() {
        if(ClientGlobals.userCommand.count > 0) {
            Log.debug(ClientGlobals.userCommand.toString());
            client.sendUDP(ClientGlobals.userCommand);
            ClientGlobals.userCommand.reset();
        }
    }

    public void sendKeyTyped(int key) {
        Network.KeyEvent keyEvent = new Network.KeyTyped();
        keyEvent.key = key;
        client.sendUDP(keyEvent);
    }

    public void sendKeyPressed(int key) {
        Network.KeyEvent keyEvent = new Network.KeyPressed();
        keyEvent.key = key;
        client.sendUDP(keyEvent);
    }

    public void sendKeyReleased(int key) {
        Network.KeyEvent keyEvent = new Network.KeyReleased();
        keyEvent.key = key;
        client.sendUDP(keyEvent);
    }

    public void sendLogin() {
        Network.Login msg = new Network.Login();
        client.sendTCP(msg);
    }

    public void sendLogout() {
        Network.Logout msg = new Network.Logout();
        client.sendTCP(msg);
    }

    private class ClientNetworkListener extends Listener {
        @Override
        public void connected(Connection connection) {
            Log.info("Connected to server: " + connection.toString());
        }

        @Override
        public void disconnected(Connection connection) {
            Log.info("Disconnected from server: " + connection.toString());
        }

        @Override
        public void received(Connection pc, Object o) {
            if(o instanceof Bag) {
                Bag bag = (Bag) o;
                for(int i = 0; i < bag.size(); i++) {
                    handlePacket(pc, bag.get(i));
                }
            } else if(o instanceof Object[]) {
                for(Object packet: (Object[]) o) {
                    handlePacket(pc, packet);
                }
            } else {
                handlePacket(pc, o);
            }
        }
        
        public void handlePacket(final Connection pc, final Object o) {
            Log.debug("Received: " + o.toString());
            if(o instanceof Message) {
                ((Message) o).receive(pc);
//                ClientGlobals.app.enqueue(new Callable() {
//                    @Override
//                    public Object call() throws Exception {
//                        ((Message) o).receive(pc);
//                        return null;
//                    }
//                });
            } else {
                Log.warn("Unknown message type: " + o.getClass().getSimpleName());
            } 
        }
    }
}
