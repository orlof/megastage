package org.megastage.ecs;

public abstract class BaseSystem {
    private long interval;
    private long wakeup;
    protected double delta;

    protected World world;

    public BaseSystem(World world, long interval) {
        this.world = world;
        this.interval = interval;
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
        if(checkProcessing()) {
            //Log.info(getClassValue().getSimpleName());
            begin();
            processSystem();
            end();
        }
    }

    protected void begin() {}
    protected void end() {}
    protected void processSystem() throws ECSException {}
    
}
