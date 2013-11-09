package org.megastage.protocol;

import com.artemis.Entity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.jme3.math.ColorRGBA;
import org.megastage.components.Mass;
import org.megastage.components.Orbit;
import org.megastage.components.Position;
import org.megastage.components.ServerSpatialSphere;
import org.megastage.components.dcpu.VirtualMonitor;
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
        kryo.register(Position.class);
        kryo.register(Mass.class);
        kryo.register(VirtualMonitor.class);
        kryo.register(ServerSpatialSphere.class);
    }

    static public class Login {}
    static public class LoginResponse {}

    static public class Logout {}

    static public abstract class KeyEvent {
        public int key;
    }
    static public class KeyPressed extends KeyEvent {}
    static public class KeyTyped extends KeyEvent {}
    static public class KeyReleased extends KeyEvent {}

    static public class SpatialSphereData {
        public int entityID;
        public ServerSpatialSphere spatial;

        public static SpatialSphereData create(Entity entity) {
            SpatialSphereData data = new SpatialSphereData();
            data.entityID = entity.getId();
            
            data.spatial = entity.getComponent(ServerSpatialSphere.class);
            
            return data;
        }
    }

    static public class SpatialMonitorData {
        public int entityID;

        public static SpatialMonitorData create(Entity entity) {
            SpatialMonitorData data = new SpatialMonitorData();
            data.entityID = entity.getId();
            return data;
        }
    }

    static public class UseData {
        public int entityID;
        public static UseData create(Entity entity) {
            UseData data = new UseData();
            data.entityID = entity.getId();
            return data;
        }
    }

    static public class StarData {
        public int entityID;
        public Position position;

        public static StarData create(Entity entity) {
            StarData data = new StarData();
            data.entityID = entity.getId();
            data.position = entity.getComponent(Position.class);
            return data;
        }
    }

    static public class OrbitData {
        public int entityID;

        public int centerID;
        public double distance;

        public static OrbitData create(Entity entity) {
            OrbitData data = new OrbitData();
            data.entityID = entity.getId();

            Orbit orbit = entity.getComponent(Orbit.class);
            data.centerID = orbit.center.getId();
            data.distance = orbit.distance;

            return data;
        }
    }

    static public class PositionData {
        public int entityID;
        
        public Position position;
        
        public static PositionData create(Entity entity) {
            PositionData data = new PositionData();
            data.entityID = entity.getId();
            data.position = entity.getComponent(Position.class);

            return data;
        }
    }
    
    static public class MassData {
        public int entityID;
        
        public Mass mass;
        
        public static MassData create(Entity entity) {
            MassData data = new MassData();
            data.entityID = entity.getId();
            data.mass = entity.getComponent(Mass.class);

            return data;
        }
    }
    
    static public class TimeData {
        public long time;

        public static TimeData create() {
            TimeData data = new TimeData();
            data.time = Globals.time;

            return data;
        }
    }

    static public class MonitorData {
        public int entityID;
        public char[] video;
        public char[] font;
        public char[] palette;

        public static MonitorData create(Entity entity) {
            MonitorData data = new MonitorData();
            data.entityID = entity.getId();

            VirtualMonitor virtualMonitor = entity.getComponent(VirtualMonitor.class);
            data.video = virtualMonitor.video.mem;
            data.font = virtualMonitor.font.mem;
            data.palette = virtualMonitor.palette.mem;

            return data;
        }
    }

    static public class KeyboardData {
        public int entityID;

        public static KeyboardData create(Entity entity) {
            KeyboardData data = new KeyboardData();
            data.entityID = entity.getId();
            return data;
        }
    }
}
