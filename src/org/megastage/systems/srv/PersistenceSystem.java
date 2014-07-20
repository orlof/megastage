package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.megastage.util.Log;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.megastage.ecs.CompType;
import org.megastage.protocol.Network;

public class PersistenceSystem extends Processor {
    Kryo kryo;    
    Output output;
    
    File stateFile = new File("gamestate.dat");
    
    byte[] buf = new byte[1024*1024];
    
    public PersistenceSystem(World world, long interval) {
        super(world, interval, CompType.PersistenceFlag);

        kryo = new Kryo();
        Network.register(kryo);
    }

    @Override
    protected void process() {
        output = new Output(buf);
        
        for(int eid = group.iterator(); eid != 0; eid = group.next()) {
            write(eid);
        }

        output.writeInt(0);
        output.flush();
        output.close();
        
        writeState(stateFile, buf, output.total());
    }

    private File writeState(File file, byte[] buf, int length) {
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file), length);
            out.write(buf, 0, length);
        } catch(IOException ex) {
            Log.error("Cannot store state. ", ex);
            return null;
        } finally {
            if(out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException ex) {}
            }
        }

        return file;
    }
    
    private void write(int eid) {
        output.writeInt(eid);
        
        for(Object comp: world.population[eid]) {
            kryo.writeClassAndObject(output, comp);
        }
    }
}
