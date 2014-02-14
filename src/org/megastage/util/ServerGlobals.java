package org.megastage.util;

import com.artemis.Component;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
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
    public static Array<RadarData> radarEchoes = new Array<>(0);
    public static Array<SOIData> soi = new Array<>(0);
    
    public static Array<Message> updates = new Array<>(100);
    //public static Array<Message> componentEvents = new Array<>();
    public static World world;
    
//    public static synchronized void addComponentEvent(Message comp) {
//        componentEvents.add(comp);
//    }
//
//    public static synchronized Array<Message> getComponentEvents() {
//        Array<Message> old = componentEvents;
//        componentEvents = new Bag<>();
//        return old;
//    }
//
    public static Array<Message> getUpdates() {
        Array<Message> old = updates;
        updates = new Array<>(100);
        return old;
    }
}

