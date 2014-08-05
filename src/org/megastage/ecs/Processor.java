package org.megastage.ecs;

import org.megastage.util.Log;

public abstract class Processor {
    private long interval;
    private long wakeup;
    protected double delta;

    protected World world;
    protected Group group;
    
    public Processor(World world, long interval, int...components) {
        this.world = world;
        this.interval = interval;
        this.group = world.createGroup(components);
    }
    
    public void initialize() {}

    protected boolean checkProcessing() {
        if(world.time >= wakeup) {
            if(wakeup == 0) {
                delta = 0.0;
            } else {
                delta = (world.time + interval - wakeup) / 1000.0;
            }
            wakeup = world.time + interval;
            return true;
        }
        return false;
    }

    protected void process() {
        //Log.info(getClass().getSimpleName());
        begin();
        for (int eid = group.iterator(); eid != 0; eid = group.next()) {
            process(eid);
        }
        end();
        //Log.info("Exit " + getClass().getSimpleName());
    }

    protected void begin() {}
    protected void end() {}
    protected void process(int eid) {}
    
}
