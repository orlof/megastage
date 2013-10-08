package org.megastage.protocol;

import com.artemis.Entity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import org.megastage.components.GravityAcceleration;
import org.megastage.components.Orbit;
import org.megastage.components.Position;
import org.megastage.components.dcpu.VirtualMonitor;

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
        kryo.register(Position.class);
        kryo.register(VirtualMonitor.class);
    }

    static public class Login {}
    static public class Logout {}

    static public class LoginResponse {}

    static public class Use {
        public int entityID;
    }

    static public abstract class KeyEvent {
        public int key;
    }
    static public class KeyPressed extends KeyEvent {}
    static public class KeyTyped extends KeyEvent {}
    static public class KeyReleased extends KeyEvent {}

    static public class StarData {
        public int entityID;
        public Position position;

        public static StarData create(Entity entity) {
            StarData starData = new StarData();
            starData.entityID = entity.getId();
            starData.position = entity.getComponent(Position.class);
            return starData;
        }
    }

    static public class OrbitData {
        public int entityID;

        public int centerID;
        public double distance;
        public double angularSpeed;

        public static OrbitData create(Entity entity) {
            OrbitData orbitData = new OrbitData();
            orbitData.entityID = entity.getId();

            Orbit orbit = entity.getComponent(Orbit.class);
            orbitData.centerID = orbit.center.getId();
            orbitData.distance = orbit.distance;
            orbitData.angularSpeed = orbit.angularSpeed;

            return orbitData;
        }
    }

    static public class MonitorData {
        public int entityID;
        public char[] video;
        public char[] font;
        public char[] palette;

        public static MonitorData create(Entity entity) {
            MonitorData monitorData = new MonitorData();
            monitorData.entityID = entity.getId();

            VirtualMonitor virtualMonitor = entity.getComponent(VirtualMonitor.class);
            monitorData.video = virtualMonitor.video.mem;
            monitorData.font = virtualMonitor.font.mem;
            monitorData.palette = virtualMonitor.palette.mem;

            return monitorData;
        }
    }

    static public class KeyboardData {
        public int entityID;

        public static KeyboardData create(Entity entity) {
            KeyboardData keyboardData = new KeyboardData();
            keyboardData.entityID = entity.getId();
            return keyboardData;
        }
    }
}
