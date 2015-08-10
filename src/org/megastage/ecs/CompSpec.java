package org.megastage.ecs;

import org.megastage.util.Log;
import org.megastage.util.MegastageException;

public class CompSpec {
    public String name;
    public Class clazz;
    public boolean replicate;
    public int cid;

    public CompSpec(String name, boolean replicate) {
        this.name = name;

        try {
            this.clazz = Class.forName("org.megastage.components." + name);
        } catch (ClassNotFoundException e) {
            Log.error(e);
            throw new MegastageException(e);
        }

        this.replicate = replicate;
    }
}
