package org.megastage.protocol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.components.BlockChange;
import org.megastage.components.transfer.EngineData;
import org.megastage.components.Mass;
import org.megastage.components.transfer.MonitorData;
import org.megastage.components.Orbit;
import org.megastage.components.FixedRotation;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.srv.SpawnPoint;
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
import org.megastage.ecs.BaseComponent;
import org.megastage.components.srv.CollisionSphere;
import org.megastage.components.DeleteFlag;
import org.megastage.components.Mode;
import org.megastage.components.UsableFlag;
import org.megastage.components.Explosion;
import org.megastage.components.Velocity;
import org.megastage.components.gfx.BatteryGeometry;
import org.megastage.components.gfx.FloppyDriveGeometry;
import org.megastage.components.gfx.ForceFieldGeometry;
import org.megastage.components.gfx.GyroscopeGeometry;
import org.megastage.components.gfx.PowerPlantGeometry;
import org.megastage.components.gfx.RadarGeometry;
import org.megastage.components.gfx.ThermalLaserGeometry;
import org.megastage.components.Identifier;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.components.transfer.GyroscopeData;
import org.megastage.components.transfer.RadarTargetData;
import org.megastage.components.transfer.ThermalLaserData;
import org.megastage.components.transfer.VirtualFloppyDriveData;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
import org.megastage.protocol.UserCommand.Build;
import org.megastage.protocol.UserCommand.ChangeBootRom;
import org.megastage.protocol.UserCommand.ChangeFloppy;
import org.megastage.protocol.UserCommand.Keyboard;
import org.megastage.protocol.UserCommand.MoveShip;
import org.megastage.protocol.UserCommand.Pick;
import org.megastage.protocol.UserCommand.Teleport;
import org.megastage.protocol.UserCommand.Unbuild;
import org.megastage.protocol.UserCommand.Unpick;
import org.megastage.util.Cube3dMap;
import org.megastage.util.RAM;
import org.megastage.util.Vector3d;

public class Network {
    public static String networkInterface = "localhost";

    public static int serverPort = 12358;

    static public void register(Kryo kryo) {
        for(Class<?> clazz: Network.class.getDeclaredClasses()) {
            kryo.register(clazz);
        }

        Class[] register = new Class[] {
            String[].class,
            char[].class,
            char[][].class,
            char[][][].class,
            Message[].class,
            Object[].class,
            BaseComponent.class,
            BatteryGeometry.class,
            BindTo.class,
            BlockChange.class,
            Build.class,
            ChangeFloppy.class,
            ChangeBootRom.class,
            CharacterGeometry.class,
            CollisionSphere.class,
            ComponentMessage.class,
            Cube3dMap.class,
            FloppyDriveGeometry.class,
            DeleteFlag.class,
            EngineData.class,
            EngineGeometry.class,
            Explosion.class,
            FixedRotation.class,
            ForceFieldData.class,
            ForceFieldGeometry.class,
            GyroscopeData.class,
            GyroscopeGeometry.class,
            Identifier.class,
            ImposterGeometry.class,
            Keyboard.class,
            Mass.class,
            Mode.class,
            MonitorData.class,
            MonitorGeometry.class,
            MoveShip.class,
            Orbit.class,
            Pick.class,
            PlanetGeometry.class,
            PlayerIDMessage.class,
            Position.class,
            PowerPlantGeometry.class,
            PPSGeometry.class,
            RadarGeometry.class,
            RadarTargetData.class,
            RAM.class,
            Rotation.class,
            ShipGeometry.class,
            SpawnPoint.class,
            SunGeometry.class,
            Teleport.class,
            ThermalLaserData.class,
            ThermalLaserGeometry.class,
            Unbuild.class,
            Unpick.class,
            UsableFlag.class,
            UserCommand.class,
            Vector3d.class,
            Velocity.class,
            VirtualFloppyDriveData.class,
            VoidGeometry.class,
        };

        for(Class clazz: register) {
            kryo.register(clazz);
        }
    }

    static public class Login extends EventMessage {}
    static public class Logout extends EventMessage {}

    static public class ComponentMessage implements Message {
        public int owner;
        public ReplicatedComponent component;

        public ComponentMessage() { /* required for Kryo */ }
        
        public ComponentMessage(int eid, ReplicatedComponent c) {
            owner = eid;
            component = c;
        }

        @Override
        public void receive(Connection pc) {
            component.receive(owner);
        }
        
        @Override
        public String toString() {
            return "ComponentMessage(" + owner + ", " + component.toString() + ")";
        }
    }
    
    static public class TimestampMessage implements Message {
        public long time;
        
        public TimestampMessage() {
            time = World.INSTANCE.time;
        }

        @Override
        public void receive(Connection pc) {
            World.INSTANCE.synchronizeClocks(time, World.INSTANCE.time);
        }

        @Override
        public String toString() {
            return "TimestampMessage(time=" + time + ")";
        }
        
        
    }

}
