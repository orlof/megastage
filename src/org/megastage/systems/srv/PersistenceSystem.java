package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.megastage.protocol.Network;
import org.megastage.util.Log;

public class PersistenceSystem extends Processor {
    Kryo kryo;
    int runCount;
    
    public static final File[] files = new File[] {
        new File("gamestate.001"),
        new File("gamestate.002"),
    };
    
    public PersistenceSystem(World world, long interval) {
        super(world, interval);

        kryo = new Kryo();
        Network.register(kryo);
    }

    @Override
    protected void process() {
        File file = files[(runCount++) % files.length];

        long stime = System.nanoTime();
        try {
            Output output = new Output(new FileOutputStream(file));

            kryo.writeClassAndObject(output, world);

            output.close();
        } catch (FileNotFoundException ex) {
            Log.error(ex);
        }
        long etime = System.nanoTime();
        Log.info("Persistence delay %d", (etime - stime));
    }
}
