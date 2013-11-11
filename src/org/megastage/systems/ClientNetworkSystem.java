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
import org.megastage.client.controls.OrbitalRotationControl;
import org.megastage.components.ClientSpatial;
import org.megastage.components.OrbitalRotation;

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

            //} else if(o instanceof Network.StarData) {
            //    handleStarDataMessage(pc, (Network.StarData) o);

            } else if(o instanceof Network.TimeData) {
                handleTimeDataMessage(pc, (Network.TimeData) o);

            } else if(o instanceof Network.OrbitData) {
                handleOrbitDataMessage(pc, (Network.OrbitData) o);

            } else if(o instanceof Network.MonitorData) {
                handleMonitorDataMessage(pc, (Network.MonitorData) o);

            } else if(o instanceof Network.KeyboardData) {
                handleKeyboardDataMessage(pc, (Network.KeyboardData) o);

            } else if(o instanceof Network.PositionData) {
                handlePositionDataMessage(pc, (Network.PositionData) o);

            } else if(o instanceof Network.SpatialMonitorData) {
                handleSpatialMonitorDataMessage(pc, (Network.SpatialMonitorData) o);

            } else if(o instanceof Network.SpatialSunData) {
                handleSpatialSunDataMessage(pc, (Network.SpatialSunData) o);

            } else if(o instanceof Network.SpatialPlanetData) {
                handleSpatialPlanetDataMessage(pc, (Network.SpatialPlanetData) o);

            } else if(o instanceof Network.MassData) {
                handleMassDataMessage(pc, (Network.MassData) o);

            } else if(o instanceof Network.OrbitalRotationData) {
                handleOrbitalRotationDataMessage(pc, (Network.OrbitalRotationData) o);

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

    private void handleSpatialMonitorDataMessage(Connection connection, Network.SpatialMonitorData data) {
        Entity entity = cems.get(data.entityID);
        //csms.setupMonitor(entity);
        csms.setupMonitor(entity, data);
    }

    private void handleSpatialSunDataMessage(Connection connection, Network.SpatialSunData data) {
        Entity entity = cems.get(data.entityID);
        //csms.setupMonitor(entity);
        csms.setupSunLikeBody(entity, data);
    }

    private void handleSpatialPlanetDataMessage(Connection connection, Network.SpatialPlanetData data) {
        Entity entity = cems.get(data.entityID);
        //csms.setupMonitor(entity);
        csms.setupPlanetLikeBody(entity, data);
    }

    private void handlePositionDataMessage(Connection connection, Network.PositionData data) {
        cems.setComponent(data.entityID, data.position);
    }

    private void handleMassDataMessage(Connection connection, Network.MassData data) {
        cems.setComponent(data.entityID, data.mass);
    }

    private void handleOrbitDataMessage(Connection connection, Network.OrbitData orbitData) {
        Orbit orbit = new Orbit();
        orbit.center = cems.get(orbitData.centerID);
        orbit.distance = orbitData.distance;

        cems.setComponent(orbitData.entityID, orbit);
    }
    
    private void handleOrbitalRotationDataMessage(Connection connection, Network.OrbitalRotationData data) {
        Entity entity = cems.get(data.entityID);
        
        OrbitalRotation orbitalRotation = new OrbitalRotation();
        orbitalRotation.angularSpeed = data.orbitalRotation.angularSpeed;
        entity.addComponent(orbitalRotation);

        ClientSpatial clientSpatial = entity.getComponent(ClientSpatial.class);
        clientSpatial.node.addControl(new OrbitalRotationControl(entity));
    }
    
    private void handleTimeDataMessage(Connection connection, Network.TimeData data) {
        Globals.timeDiff = data.time - System.currentTimeMillis();
    }
}
