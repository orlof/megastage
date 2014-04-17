package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import org.megastage.util.GlobalTime;

public class SystemTemplate extends EntitySystem {
    private long interval;
    private long wakeup;
    
    private double delta;

    public SystemTemplate(Aspect aspect) {
        super(aspect);
    }

    public SystemTemplate(Aspect aspect, long interval) {
        super(aspect);
        this.interval = interval;
    }

    @Override
    protected boolean checkProcessing() {
        if(GlobalTime.value >= wakeup) {
            delta = (GlobalTime.value + interval - wakeup) / 1000.0;
            wakeup = GlobalTime.value + interval;
            return true;
        }
        return false;
    }

    @Override
    protected void processEntities(Array<Entity> entities) {
        for (int i = 0, s = entities.size; s > i; i++) {
            process(entities.get(i));
        }
    }

    protected void process(Entity e) {};
}
