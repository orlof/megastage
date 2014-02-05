package org.megastage.util;

import com.artemis.World;
import com.artemis.utils.Bag;
import org.megastage.protocol.Message;
import org.megastage.systems.srv.RadarEchoSystem.RadarData;
import org.megastage.systems.srv.SphereOfInfluenceSystem.SOIData;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:50
 */
public class ServerGlobals {
    public static Bag<RadarData> radarEchoes = new Bag(0);
    public static Bag<SOIData> soi = new Bag(0);
    
    public static Bag updates = new Bag(100);
    public static Bag<Message> componentEvents = new Bag<>();
    public static World world;
    
    public static synchronized void addComponentEvent(Message comp) {
        componentEvents.add(comp);
    }

    public static synchronized Bag<Message> getComponentEvents() {
        Bag<Message> old = componentEvents;
        componentEvents = new Bag<>();
        return old;
    }

    public static Bag getUpdates() {
        Bag old = updates;
        updates = new Bag(100);
        return old;
    }
}

