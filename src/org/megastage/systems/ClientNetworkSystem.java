package org.megastage.systems;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.VoidEntitySystem;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.megastage.components.Orbit;
import org.megastage.components.Position;
import org.megastage.protocol.Network;
import org.megastage.util.Globals;
import org.megastage.components.client.VirtualMonitorView;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public class ClientNetworkSystem extends VoidEntitySystem {
    private final static Logger LOG = Logger.getLogger(ClientNetworkSystem.class.getName());

    private Client client;

    public ClientNetworkSystem() {
        super();

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
    protected void processSystem() {
    }

    @Override
    protected boolean checkProcessing() {
        return false;
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

    public void sendUse() {
        Network.Use use = new Network.Use();
        use.entityID = 0;
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
            LOG.info("Connected to server: " + connection.toString());
        }

        @Override
        public void disconnected(Connection connection) {
            LOG.info("Disconnected from server: " + connection.toString());
        }

        @Override
        public void received(Connection pc, Object o) {
            LOG.info("Received: " + o.getClass().getName());

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

            } else {
                LOG.severe("Unknown message received");
            }
        }
    }

    private void handleKeyboardDataMessage(Connection pc, Network.KeyboardData keyboardData) {
        Network.Use use = new Network.Use();
        use.entityID = keyboardData.entityID;
        client.sendUDP(use);
    }

    @Mapper ComponentMapper<VirtualMonitorView> viewMapper;

    private boolean first = true;
    private void handleMonitorDataMessage(Connection connection, Network.MonitorData monitorData) {
        Entity monitor = ensureClientEntity(monitorData.entityID);

        VirtualMonitorView view = viewMapper.get(monitor);
        if(view == null) {
            view = new VirtualMonitorView();
            monitor.addComponent(view);

            world.getSystem(ClientRenderSystem.class).panel.image = view.img;
        }

        view.update(monitorData);
    }

    private void handleStarDataMessage(Connection connection, Network.StarData starData) {
        Entity star = ensureClientEntity(starData.entityID);
        star.addComponent(starData.position);
    }

    private void handleOrbitDataMessage(Connection connection, Network.OrbitData orbitData) {
        Orbit orbit = new Orbit();
        orbit.center = ensureClientEntity(orbitData.centerID);
        orbit.angularSpeed = orbitData.angularSpeed;
        orbit.distance = orbitData.distance;

        Entity satellite = ensureClientEntity(orbitData.entityID);
        satellite.addComponent(orbit);
    }


    HashMap<Integer, Entity> serverIDToClientEntity = new HashMap<Integer, Entity>();
    HashMap<Integer, Integer> clientIDToServerID = new HashMap<Integer, Integer>();

    private Entity ensureClientEntity(int serverEntityID) {
        Entity clientEntity = serverIDToClientEntity.get(serverEntityID);
        if(clientEntity == null) {
            clientEntity = world.createEntity();
            world.addEntity(clientEntity);
            serverIDToClientEntity.put(serverEntityID, clientEntity);
            clientIDToServerID.put(clientEntity.getId(), serverEntityID);
        }
        return clientEntity;
    }

}
