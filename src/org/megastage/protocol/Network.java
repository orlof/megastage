package org.megastage.protocol;

import com.artemis.Entity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import org.megastage.components.Physical;
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
    }

    static public class Login {}

    static public class LoginResponse {
        public int entityID;

        public static LoginResponse create(Entity entity) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.entityID = entity.getId();
            return loginResponse;
        }
    }

    static public class Use {
        public int entityID;
    }

    static public class KeyEvent {
        public int key;
    }
    static public class KeyPressed extends KeyEvent {}
    static public class KeyTyped extends KeyEvent {}
    static public class KeyReleased extends KeyEvent {}

    static public class StarData {
        public int entityID;
        public Position position;
        public Physical physical;

        public static StarData create(Entity entity) {
            StarData starData = new StarData();
            starData.entityID = entity.getId();
            starData.position = entity.getComponent(Position.class);
            starData.physical = entity.getComponent(Physical.class);
            return starData;
        }
    }

    static public class MonitorData {
        public int entityID;
        public VirtualMonitor monitor;

        public static MonitorData create(Entity entity) {
            MonitorData monitorData = new MonitorData();
            monitorData.entityID = entity.getId();
            monitorData.monitor = entity.getComponent(VirtualMonitor.class);
            return monitorData;
        }
    }
}
