package org.megastage.ecs;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import org.megastage.components.gfx.CharacterGeometry;
import org.megastage.protocol.Network;
import org.megastage.util.Log;

public class KryoWorld extends World {
    private Kryo kryo;
    private int runCount;
    
    private static final File[] files = new File[] {
        new File("gamestate.001"),
        new File("gamestate.002"),
    };

    public KryoWorld() {
        this(10000, CompType.size);
    }

    public KryoWorld(int entityCapacity, int componentCapacity) {
        super(entityCapacity, componentCapacity);
        
        kryo = new Kryo();
        Network.register(kryo);
    }

    public boolean load() {
        File latest = getLatestFile(files);
        
        if(latest != null) {
            try {
                try (Input input = new Input(new FileInputStream(latest))) {
                    time = kryo.readObject(input, long.class);
                    size = kryo.readObject(input, int.class);
                    population = kryo.readObject(input, BaseComponent[][].class);
                    next = kryo.readObject(input, int[].class);
                    prev = kryo.readObject(input, int[].class);
                    free = kryo.readObject(input, boolean[].class);
                }
                updateAll();

                offset = time - System.currentTimeMillis();
                
                dump(latest);
                return true;
            } catch (FileNotFoundException ex) {
                Log.error(ex);
                return false;
            }
        }
        
        return false;
    }

    public void save() {
        File file = files[(runCount++) % files.length];

        long stime = System.nanoTime();
        try {
            try (Output output = new Output(new FileOutputStream(file))) {
                kryo.writeObject(output, time);
                kryo.writeObject(output, size);
                kryo.writeObject(output, population);
                kryo.writeObject(output, next);
                kryo.writeObject(output, prev);
                kryo.writeObject(output, free);
            }
        } catch (FileNotFoundException ex) {
            Log.error(ex);
        }
        long etime = System.nanoTime();
        Log.info("Persistence delay %d", (etime - stime));
    }

    private File getLatestFile(File[] files) {
        File latest = null;
        for(File file: files) {
            if(file.exists() && (latest == null || file.lastModified() > latest.lastModified())) {
                latest = file;
            }
        }
        return latest;
    }
    
    private void dump(File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
 
        Log.info("Loaded gamestate: %s", sdf.format(file.lastModified()));
        long scale = 24*60*60*1000;
        long day = time / scale;
        time -= day * scale;
        
        scale = 60*60*1000;
        long hour = time / scale;
        time -= hour * scale;

        scale = 60*1000;
        long min = time / scale;
        time -= min * scale;

        scale = 1000;
        long sec = time / scale;
        time -= sec * scale;

        Log.info("Game time: %d %02d:%02d:%02d", day, hour, min, sec);
        Log.info("Entities: %d", size);
    }
    
}
