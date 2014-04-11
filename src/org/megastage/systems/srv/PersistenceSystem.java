package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.megastage.components.PersistenceFlag;
import org.megastage.protocol.Network;
import org.megastage.util.Time;

public class PersistenceSystem extends EntitySystem {
    private long interval;
    private long wakeup;
    
    private double delta;

    Kryo kryo;    
    Output output;
    
    File stateFile = new File("gamestate.dat");
    
    byte[] buf = new byte[1024*1024];
    Array<Component> comps = new Array<>(50);
    
    public PersistenceSystem() {
        super(Aspect.getAspectForAll(PersistenceFlag.class));
    }

    public PersistenceSystem(Aspect aspect, long interval) {
        super(Aspect.getAspectForAll(PersistenceFlag.class));
        this.interval = interval;
        
        kryo = new Kryo();
        Network.registerSpecials(kryo);
    }

    @Override
    protected boolean checkProcessing() {
        if(Time.value >= wakeup) {
            delta = (Time.value + interval - wakeup) / 1000.0;
            wakeup = Time.value + interval;
            return true;
        }
        return false;
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        output = new Output(buf);
        
        for (int i = 0, s = entities.size; s > i; i++) {
            process(entities.get(i));
        }

        output.writeInt(0);
        output.flush();
        output.close();
        
        writeState(stateFile, buf, output.total());
    }

    protected void process(Entity e) {
        write(e);
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
    
    private void write(Entity e) {
        output.writeInt(e.id);
        
        e.getComponents(comps);
        output.writeInt(comps.size);

        for(Component c: comps) {
            kryo.writeClassAndObject(output, c);
        }
    }
}
