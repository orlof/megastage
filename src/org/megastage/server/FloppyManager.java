// TODO THIS IS JUST A PLACE HOLDER IMPL
package org.megastage.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import org.megastage.components.dcpu.FloppyDisk;

public class FloppyManager {
    public static final HashMap<String, char[]> bootroms = new HashMap<>();
    public static final HashMap<String, FloppyDisk> floppies = new HashMap<>();

    static {
        File folder = new File("media");
        for(File f: folder.listFiles()) {
            if(f.isFile()) {
                if(f.getName().endsWith(".bin")) {
                    bootroms.put(f.getName(), load(f, 65536));
                } else if(f.getName().endsWith(".d16")) {
                    FloppyDisk fd = new FloppyDisk();
                    fd.load(f);
                    floppies.put(f.getName(), fd);
                }
            }
        }
    }

    public static String[] getBootromNames() {
        return bootroms.keySet().toArray(new String[bootroms.size()]);
    }
    
    public static String[] getFloppyNames() {
        return floppies.keySet().toArray(new String[floppies.size()]);
    }
    
    private static char[] load(File file, int len) {
        char[] data = new char[len];
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            int i = 0;
            for (; i < data.length; i++) {
                data[i] = dis.readChar();
            }
            dis.close();
        } catch (EOFException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
