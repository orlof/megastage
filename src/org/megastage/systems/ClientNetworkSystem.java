package org.megastage.systems;

import com.artemis.Entity;
import com.artemis.systems.VoidEntitySystem;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import org.megastage.protocol.Network;
import org.megastage.util.Globals;

import java.io.IOException;
import org.megastage.protocol.Message;

public class ClientNetworkSystem extends VoidEntitySystem {
    private Client client;

    public ClientEntityManagerSystem cems;
    public ClientSpatialManagerSystem csms;

    @Override
    protected void initialize() {
        this.cems = world.getSystem(ClientEntityManagerSystem.class);
        this.csms = world.getSystem(ClientSpatialManagerSystem.class);

        client = new Client();
        Network.register(client);

        Thread kryoThread = new Thread(client);
        kryoThread.setDaemon(true);
        kryoThread.start();

        try {
            client.connect(5000, Globals.serverHost, Globals.serverPort, Globals.serverPort+1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.addListener(new ClientNetworkListener());
    }

    @Override
    protected void processSystem() {}

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

    public void sendAnalogInput(String name, float value, float tpf) {
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
        
        public void handlePacket(Connection pc, Object o) {
            Log.info("Received: " + o.toString());
            
            if(o instanceof Message) {
                ((Message) o).receive(ClientNetworkSystem.this, pc);
            } else {
                Log.warn("Unknown message type");
            }
            
        }
    }
}
