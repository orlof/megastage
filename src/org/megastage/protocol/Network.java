package org.megastage.protocol;

import com.artemis.Entity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.transfer.EngineData;
import org.megastage.components.Mass;
import org.megastage.components.transfer.MonitorData;
import org.megastage.components.Orbit;
import org.megastage.components.FixedRotation;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.SpawnPoint;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.gfx.CharacterGeometry;
import org.megastage.components.gfx.EngineGeometry;
import org.megastage.components.gfx.ImposterGeometry;
import org.megastage.components.gfx.MonitorGeometry;
import org.megastage.components.gfx.PlanetGeometry;
import org.megastage.components.gfx.PPSGeometry;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.components.gfx.SunGeometry;
import org.megastage.components.gfx.VoidGeometry;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.components.DeleteFlag;
import org.megastage.components.Mode;
import org.megastage.components.UsableFlag;
import org.megastage.components.Explosion;
import org.megastage.components.Velocity;
import org.megastage.components.gfx.ForceFieldGeometry;
import org.megastage.components.gfx.GyroscopeGeometry;
import org.megastage.components.gfx.RadarGeometry;
import org.megastage.components.gfx.ThermalLaserGeometry;
import org.megastage.components.srv.Identifier;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.components.transfer.GyroscopeData;
import org.megastage.components.transfer.RadarTargetData;
import org.megastage.components.transfer.ThermalLaserData;
import org.megastage.protocol.UserCommand.Build;
import org.megastage.protocol.UserCommand.Keyboard;
import org.megastage.protocol.UserCommand.MoveShip;
import org.megastage.protocol.UserCommand.Pick;
import org.megastage.protocol.UserCommand.Teleport;
import org.megastage.protocol.UserCommand.Unbuild;
import org.megastage.protocol.UserCommand.Unpick;
import org.megastage.util.Cube3dMap;
import org.megastage.util.Cube3dMap.BlockChange;
import org.megastage.util.ID;
import org.megastage.util.RAM;
import org.megastage.util.Vector3d;

public class Network {
    public static String networkInterface = "localhost";

    public static int serverPort = 12358;

    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        for(Class<?> clazz: Network.class.getDeclaredClasses()) {
            kryo.register(clazz);
        }

        kryo.register(char[].class);
        kryo.register(char[][].class);
        kryo.register(char[][][].class);
        kryo.register(Object[].class);
        kryo.register(BaseComponent.class);
        kryo.register(BindTo.class);
        kryo.register(BlockChange.class);
        kryo.register(Build.class);
        kryo.register(CharacterGeometry.class);
        kryo.register(ComponentMessage.class);
        kryo.register(Cube3dMap.class);
        kryo.register(DeleteFlag.class);
        kryo.register(EngineData.class);
        kryo.register(EngineGeometry.class);
        kryo.register(Explosion.class);
        kryo.register(FixedRotation.class);
        kryo.register(ForceFieldData.class);
        kryo.register(ForceFieldGeometry.class);
        kryo.register(GyroscopeData.class);
        kryo.register(GyroscopeGeometry.class);
        kryo.register(Identifier.class);
        kryo.register(ImposterGeometry.class);
        kryo.register(Keyboard.class);
        kryo.register(Mass.class);
        kryo.register(Mode.class);
        kryo.register(MonitorData.class);
        kryo.register(MonitorGeometry.class);
        kryo.register(MoveShip.class);
        kryo.register(Orbit.class);
        kryo.register(Pick.class);
        kryo.register(PlanetGeometry.class);
        kryo.register(PlayerIDMessage.class);
        kryo.register(Position.class);
        kryo.register(PPSGeometry.class);
        kryo.register(RadarGeometry.class);
        kryo.register(RadarTargetData.class);
        kryo.register(RAM.class);
        kryo.register(Rotation.class);
        kryo.register(ShipGeometry.class);
        kryo.register(SpawnPoint.class);
        kryo.register(SunGeometry.class);
        kryo.register(Teleport.class);
        kryo.register(ThermalLaserData.class);
        kryo.register(ThermalLaserGeometry.class);
        kryo.register(Unbuild.class);
        kryo.register(Unpick.class);
        kryo.register(UsableFlag.class);
        kryo.register(UserCommand.class);
        kryo.register(Vector3d.class);
        kryo.register(Velocity.class);
        kryo.register(VoidGeometry.class);
    }

    static public class Login extends EventMessage {}
    static public class Logout extends EventMessage {}

    static public class ComponentMessage implements Message {
        public int owner;
        public BaseComponent component;

        public ComponentMessage() { /* required for Kryo */ }
        
        public ComponentMessage(Entity entity, BaseComponent c) {
            owner = entity.id;
            component = c;
        }

        @Override
        public void receive(Connection pc) {
            Entity entity = ClientGlobals.artemis.toClientEntity(owner);
            owner = entity.id;
            
            component.receive(pc, entity);
        }
        
        @Override
        public String toString() {
            return "ComponentMessage(" + owner + ", " + component.toString() + ")";
        }
    }
}
