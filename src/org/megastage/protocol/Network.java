package org.megastage.protocol;

import com.artemis.Entity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import org.megastage.components.EntityComponent;
import org.megastage.components.Mass;
import org.megastage.components.MonitorData;
import org.megastage.components.Orbit;
import org.megastage.components.FixedRotation;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.server.PlanetGeometry;
import org.megastage.components.server.ShipGeometry;
import org.megastage.components.server.SunGeometry;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.util.Globals;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 10/2/13
 * Time: 7:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class Network {
    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        for(Class<?> clazz: Network.class.getDeclaredClasses()) {
            kryo.register(clazz);
        }

        kryo.register(char[].class);
        kryo.register(Object[].class);
        kryo.register(EntityData.class);
        kryo.register(Mass.class);
        kryo.register(MonitorData.class);
        kryo.register(Orbit.class);
        kryo.register(FixedRotation.class);
        kryo.register(Position.class);
        kryo.register(Rotation.class);
        kryo.register(PlanetGeometry.class);
        kryo.register(SunGeometry.class);
        kryo.register(ShipGeometry.class);
    }

    static public abstract class EventMessage implements Message {
        public void receive(ClientNetworkSystem system, Connection pc) {}
    }
    
    static public class Login extends EventMessage {}
    static public class Logout extends EventMessage {}
    
    static public class LoginResponse extends EventMessage {
        private int id = 0;
        
        public LoginResponse() { /* Kryonet */ }
        public LoginResponse(int id) {
            this.id = id;
        }

        public void receive(ClientNetworkSystem system, Connection pc) {
            Globals.fixedEntity = system.cems.get(id);
        }
        
        public String toString() {
            return "LoginResponse(" + id + ")";
        }
    }

    static public abstract class KeyEvent extends EventMessage {
        public int key;
    }
    static public class KeyPressed extends KeyEvent {}
    static public class KeyTyped extends KeyEvent {}
    static public class KeyReleased extends KeyEvent {}

    static public class AnalogInput extends EventMessage {
        public String name;
        public float value;
        public float tpf;
    }
    
    static public class EntityData implements Message {
        public int entityID;
        public EntityComponent component;

        public EntityData() { /* required for Kryo */ }
        
        public EntityData(Entity entity, EntityComponent c) {
            entityID = entity.getId();
            component = c;
        }

        @Override
        public void receive(ClientNetworkSystem system, Connection pc) {
            Entity entity = system.cems.get(entityID);
            component.receive(system, pc, entity);
        }
        
        public String toString() {
            return "EntityData(" + entityID + ", " + component.toString() + ")";
        }
    }
    
    static public class TimeData extends EventMessage {
        public long time = Globals.time;

        @Override
        public void receive(ClientNetworkSystem system, Connection pc) {
            Globals.timeDiff = time - System.currentTimeMillis();
        }
    }
}
