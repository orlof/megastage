package org.megastage.protocol;

import com.artemis.Entity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
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
import org.megastage.components.CollisionSphere;
import org.megastage.components.DeleteFlag;
import org.megastage.components.Energy;
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
import org.megastage.components.srv.Identifier;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.components.transfer.GyroscopeData;
import org.megastage.components.transfer.RadarTargetData;
import org.megastage.components.transfer.ThermalLaserData;
import org.megastage.components.transfer.VirtualFloppyDriveData;
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
import org.megastage.util.Cube3dMap.BlockChange;
import org.megastage.util.RAM;
import org.megastage.util.Vector3d;

public class Network {
    public static String networkInterface = "localhost";

    public static int serverPort = 12358;

    static public void register(Kryo kryo, int purpose) {
        for(Class<?> clazz: Network.class.getDeclaredClasses()) {
            kryo.register(clazz);
        }

        Object[][] register = new Object[][] {
            new Object[] {String[].class, null, null},
            new Object[] {char[].class, null, null},
            new Object[] {char[][].class, null, null},
            new Object[] {char[][][].class, null, null},
            new Object[] {Object[].class, null, null},
            new Object[] {BaseComponent.class, null, null},
            new Object[] {BatteryGeometry.class, null, null},
            new Object[] {BindTo.class, null, null},
            new Object[] {BlockChange.class, null, null},
            new Object[] {Build.class, null, null},
            new Object[] {ChangeFloppy.class, null, null},
            new Object[] {ChangeBootRom.class, null, null},
            new Object[] {CharacterGeometry.class, null, null},
            new Object[] {CollisionSphere.class, null, null},
            new Object[] {ComponentMessage.class, null, null},
            new Object[] {Cube3dMap.class, null, null},
            new Object[] {FloppyDriveGeometry.class, null, null},
            new Object[] {DeleteFlag.class, null, null},
            new Object[] {Energy.class, null, null},
            new Object[] {EngineData.class, null, null},
            new Object[] {EngineGeometry.class, null, null},
            new Object[] {Explosion.class, null, null},
            new Object[] {FixedRotation.class, null, null},
            new Object[] {ForceFieldData.class, null, null},
            new Object[] {ForceFieldGeometry.class, null, null},
            new Object[] {GyroscopeData.class, null, null},
            new Object[] {GyroscopeGeometry.class, null, null},
            new Object[] {Identifier.class, null, null},
            new Object[] {ImposterGeometry.class, null, null},
            new Object[] {Keyboard.class, null, null},
            new Object[] {Mass.class, null, null},
            new Object[] {Mode.class, null, null},
            new Object[] {MonitorData.class, null, null},
            new Object[] {MonitorGeometry.class, null, null},
            new Object[] {MoveShip.class, null, null},
            new Object[] {Orbit.class, null, null},
            new Object[] {Pick.class, null, null},
            new Object[] {PlanetGeometry.class, null, null},
            new Object[] {PlayerIDMessage.class, null, null},
            new Object[] {Position.class, null, null},
            new Object[] {PowerPlantGeometry.class, null, null},
            new Object[] {PPSGeometry.class, null, null},
            new Object[] {RadarGeometry.class, null, null},
            new Object[] {RadarTargetData.class, null, null},
            new Object[] {RAM.class, null, null},
            new Object[] {Rotation.class, null, null},
            new Object[] {ShipGeometry.class, null, null},
            new Object[] {SpawnPoint.class, null, null},
            new Object[] {SunGeometry.class, null, null},
            new Object[] {Teleport.class, null, null},
            new Object[] {ThermalLaserData.class, null, null},
            new Object[] {ThermalLaserGeometry.class, null, null},
            new Object[] {Unbuild.class, null, null},
            new Object[] {Unpick.class, null, null},
            new Object[] {UsableFlag.class, null, null},
            new Object[] {UserCommand.class, null, null},
            new Object[] {Vector3d.class, null, null},
            new Object[] {Velocity.class, null, null},
            new Object[] {VirtualFloppyDriveData.class, null, null},
            new Object[] {VoidGeometry.class, null, null},
        };

        for(Object[] data: register) {
            Class clazz = (Class) data[0];
            Serializer s = (Serializer) data[purpose+1];
            if(s == null) {
                kryo.register(clazz);
            } else {
                kryo.register(clazz, s);
            }
        }
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
