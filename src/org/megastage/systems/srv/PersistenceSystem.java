package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import com.esotericsoftware.kryo.Kryo;
import java.io.File;
import org.megastage.ecs.KryoWorld;
import org.megastage.protocol.Network;

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
        ((KryoWorld) world).save();
    }
}
