package org.megastage.ecs;

public class CompSpec {
    public String name;
    public Class clazz;
    public int cid;
    public boolean replicate;

    public CompSpec(String name, Class clazz, int cid, boolean replicate) {
        this.name = name;
        this.clazz = clazz;
        this.cid = cid;
        this.replicate = replicate;
    }
}
