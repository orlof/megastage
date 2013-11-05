package org.megastage.systems;

import com.artemis.Entity;
import com.artemis.systems.VoidEntitySystem;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.Orbit;
import org.megastage.protocol.Network;
import org.megastage.util.Globals;
import org.megastage.components.ClientVideoMemory;

import java.io.IOException;

public class ClientNetworkSystem extends VoidEntitySystem {
    private Client client;

    private ClientEntityManagerSystem cems;
    private ClientSpatialManagerSystem csms;

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

    public void sendUse(int entityID) {
        Network.UseData use = new Network.UseData();
        use.entityID = cems.convert(entityID);
        client.sendUDP(use);
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
        
        public void handlePacket(Connection pc, Object o) {
            Log.info("Received: " + o.getClass().getName());

            if(o instanceof Network.LoginResponse) {
                // nothing to do - for now

            } else if(o instanceof Network.StarData) {
                handleStarDataMessage(pc, (Network.StarData) o);

            } else if(o instanceof Network.OrbitData) {
                handleOrbitDataMessage(pc, (Network.OrbitData) o);

            } else if(o instanceof Network.MonitorData) {
                handleMonitorDataMessage(pc, (Network.MonitorData) o);

            } else if(o instanceof Network.KeyboardData) {
                handleKeyboardDataMessage(pc, (Network.KeyboardData) o);

            } else if(o instanceof Network.PositionData) {
                handlePositionDataMessage(pc, (Network.PositionData) o);

            } else if(o instanceof Network.SpatialData) {
                handleSpatialDataMessage(pc, (Network.SpatialData) o);

            } else {
                Log.warn("Unknown message received");
            }
        }
    }

    private void handleKeyboardDataMessage(Connection pc, Network.KeyboardData keyboardData) {
        Network.UseData use = new Network.UseData();
        use.entityID = keyboardData.entityID;
        client.sendUDP(use);
    }

    private void handleMonitorDataMessage(Connection connection, Network.MonitorData data) {
        ClientVideoMemory videoMemory = cems.getComponent(data.entityID, ClientVideoMemory.class);
        videoMemory.update(data);
    }

    private void handleSpatialDataMessage(Connection connection, Network.SpatialData data) {
        Entity entity = cems.get(data.entityID);
        csms.setupMonitor(entity);
    }

    private void handlePositionDataMessage(Connection connection, Network.PositionData data) {
        cems.setComponent(data.entityID, data.position);
    }

    private void handleStarDataMessage(Connection connection, Network.StarData data) {
        cems.setComponent(data.entityID, data.position);
    }

    private void handleOrbitDataMessage(Connection connection, Network.OrbitData orbitData) {
        Orbit orbit = new Orbit();
        orbit.center = cems.get(orbitData.centerID);
        orbit.angularSpeed = orbitData.angularSpeed;
        orbit.distance = orbitData.distance;

        cems.setComponent(orbitData.entityID, orbit);
    }
}
