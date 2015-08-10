package org.megastage.protocol;

import com.cubes.Vector3Int;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.jme3.math.Vector3f;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class Network {
    public static String networkInterface = "localhost";

    public static int serverPort = 57463;

    static public void registerClassesToKryo(Kryo kryo) {

        // registerClassesToKryo classes marked as carriers

        Set<Class<? extends Carrier>> carriers = new Reflections("org.megastage").getSubTypesOf(Carrier.class);
        ArrayList<Class<? extends Carrier>> list = new ArrayList<>(carriers);

        Collections.sort(list, new Comparator<Class<? extends Carrier>>() {
            @Override
            public int compare(Class<? extends Carrier> o1, Class<? extends Carrier> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for(Class carrier: list) {
            kryo.register(carrier);
        }

        // registerClassesToKryo classes from "3rd party" libraries

        kryo.register(String[].class);
        kryo.register(char[].class);
        kryo.register(char[][].class);
        kryo.register(char[][][].class);
        kryo.register(float[].class);
        kryo.register(Vector3f.class);
        kryo.register(Vector3Int.class);
    }


    static public class TimestampMessage implements Message, Carrier {
        public long time;
        
        public TimestampMessage() {
            time = World.INSTANCE.time;
        }

        @Override
        public void receive(Connection pc) {
            // TODO
            // This updates end time to all position predictor components
            // Position receive updates end position to position predictor components
            // Position predictor processor updates position components based on position predictor components
            // position control updates spatial from position component
            // position predictor processor can be integrated into position control
            // perhaps control should always copy position from component
            // remember flappitaulu, you must use server time
            ClientGlobals.syncTime = World.INSTANCE.time;
        }

        @Override
        public String toString() {
            return "TimestampMessage(time=" + time + ")";
        }
    }
}
