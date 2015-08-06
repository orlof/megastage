package org.megastage.ecs;

import org.megastage.util.Log;

public class CompSpec {
    public String name;
    public Class clazz;
//    public int cid;
    public boolean replicate;
    public int cid;

    public CompSpec(String name, String clazzName, boolean replicate) {
        this.name = name;

        try {
            this.clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            Log.error(e);
        }

        this.replicate = replicate;
    }
}
