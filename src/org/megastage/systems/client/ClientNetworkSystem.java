package org.megastage.systems.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import org.megastage.protocol.Network;

import java.io.IOException;
import org.megastage.protocol.Message;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;

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
            if(o instanceof Object[]) {
                for(Object packet: (Object[]) o) {
                    handlePacket(pc, packet);
                }
            } else {
                handlePacket(pc, o);
            }
        }
        
        public void handlePacket(final Connection pc, final Object o) {
            if(o instanceof Message) {
                Log.info(o.toString());
                final Message msg = (Message) o;
                msg.receive(world, pc);
            } else {
                Log.warn("Unknown message type: " + o.getClass().getSimpleName());
            } 
        }
    }
}
