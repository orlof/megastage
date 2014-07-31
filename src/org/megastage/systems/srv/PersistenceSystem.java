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
    
    File stateFile = new File("gamestate.dat");
    
    public PersistenceSystem(World world, long interval) {
        super(world, interval);

        kryo = new Kryo();
        Network.register(kryo);
    }

    @Override
    protected void process() {
        long stime = System.nanoTime();
        try {
            Output output = new Output(new FileOutputStream(stateFile));

            kryo.writeClassAndObject(output, world.population);

            output.close();
        } catch (FileNotFoundException ex) {
            Log.error(ex);
        }
        long etime = System.nanoTime();
        Log.info("Persistence delay %d", (etime - stime));
    }
}
