package org.megastage.protocol;

import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import org.megastage.components.EngineData;
import org.megastage.components.Mass;
import org.megastage.components.MonitorData;
import org.megastage.components.Orbit;
import org.megastage.components.FixedRotation;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.SpawnPoint;
import org.megastage.components.server.BindTo;
import org.megastage.components.server.CharacterGeometry;
import org.megastage.components.server.EngineGeometry;
import org.megastage.components.server.MonitorGeometry;
import org.megastage.components.server.PlanetGeometry;
import org.megastage.components.server.ShipGeometry;
import org.megastage.components.server.SunGeometry;
import org.megastage.components.server.VoidGeometry;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.components.server.Mode;
import org.megastage.components.server.UsableFlag;
import org.megastage.util.RAM;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 10/2/13
 * Time: 7:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class Network {
    public static String networkInterface = "localhost";

    public static int serverPort = 12358;

    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        for(Class<?> clazz: Network.class.getDeclaredClasses()) {
            kryo.register(clazz);
        }

        kryo.register(char[].class);
        kryo.register(boolean[].class);
        kryo.register(boolean[][].class);
        kryo.register(boolean[][][].class);
        kryo.register(Object[].class);
        kryo.register(ComponentMessage.class);
        kryo.register(Bag.class);
        kryo.register(BaseComponent.class);
        kryo.register(Mass.class);
        kryo.register(Mode.class);
        kryo.register(MonitorData.class);
        kryo.register(EngineData.class);
        kryo.register(Orbit.class);
        kryo.register(FixedRotation.class);
        kryo.register(Position.class);
        kryo.register(Rotation.class);
        kryo.register(SpawnPoint.class);
        kryo.register(PlanetGeometry.class);
        kryo.register(SunGeometry.class);
        kryo.register(ShipGeometry.class);
        kryo.register(MonitorGeometry.class);
        kryo.register(EngineGeometry.class);
        kryo.register(RAM.class);
        kryo.register(BindTo.class);
        kryo.register(VoidGeometry.class);
        kryo.register(CharacterGeometry.class);
        kryo.register(Vector.class);
        kryo.register(LoginResponse.class);
        kryo.register(UsableFlag.class);
        kryo.register(UserCommand.class);
    }

    static public class Login extends EventMessage {}
    static public class Logout extends EventMessage {}
    
    static public class ComponentMessage implements Message {
        public int owner;
        public BaseComponent component;

        public ComponentMessage() { /* required for Kryo */ }
        
        public ComponentMessage(Entity entity, BaseComponent c) {
            owner = entity.getId();
            component = c;
        }

        @Override
        public void receive(Connection pc) {
            Entity entity = ClientGlobals.artemis.toClientEntity(owner);
            owner = entity.getId();
            
            component.receive(pc, entity);
        }
        
        public String toString() {
            return "ComponentMessage(" + owner + ", " + component.toString() + ")";
        }
    }
}
