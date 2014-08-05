package org.megastage.systems.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.megastage.util.Log;
import org.megastage.protocol.Network;

import java.io.IOException;
import org.megastage.protocol.Message;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;
import org.megastage.util.Bag;

public class ClientNetworkSystem extends Processor {
    private Client client;

    public ClientNetworkSystem(World world, long interval) {
        super(world, interval, CompType.NONE);
    }

    @Override
    public void initialize() {
        client = new Client(16*1024, 8*1024);
        Network.register(client.getKryo());

        Thread kryoThread = new Thread(client);
        kryoThread.setDaemon(true);
        kryoThread.start();

        try {
            client.connect(5000, ClientGlobals.serverHost, Network.serverPort, Network.serverPort+1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.addListener(new ClientNetworkListener());
        
        /*
        client.updateReturnTripTime();
        while(client.getReturnTripTime() == -1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
        ClientGlobals.timeDiff -= client.getReturnTripTime();
        */
    }

    @Override
    protected void process() {
        if(ClientGlobals.userCommand.count > 0) {
            client.sendUDP(ClientGlobals.userCommand);
            ClientGlobals.userCommand.reset();
        }
        
        handleReceived();
    }

    public void sendLogin() {
        Network.Login msg = new Network.Login();
        client.sendTCP(msg);
    }

    public void sendLogout() {
        Network.Logout msg = new Network.Logout();
        client.sendTCP(msg);
    }

    private Bag received = new Bag(100);

    public void handleReceived() {
        // Log.info("" + received.size());
        Bag oldBag = received;
        received = new Bag(100);
        for(Object o: oldBag) {
            handleReceivedPacket(null, o);
        }
    }
    
    public void handleReceivedPacket(Connection c, Object o) {
        if(o instanceof Object[]) {
            for(Object packet: (Object[]) o) {
                handlePacket(c, packet);
            }
        } else {
            handlePacket(c, o);
        }
    }
    
    public void handlePacket(final Connection pc, final Object o) {
        if(o instanceof Message) {
            Log.debug(o.toString());
            Message msg = (Message) o;
            msg.receive(pc);
        } else {
            Log.warn("Unknown message type: " + o.getClass().getSimpleName());
        } 
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
            try {
                received.add(o);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
