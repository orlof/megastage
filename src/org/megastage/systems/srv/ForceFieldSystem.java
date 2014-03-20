package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.dcpu.VirtualForceField;
import static org.megastage.components.dcpu.VirtualForceField.STATUS_FIELD_ACTIVE;
import static org.megastage.components.dcpu.VirtualForceField.STATUS_FIELD_FORMING;
import static org.megastage.components.dcpu.VirtualForceField.STATUS_POWER_OFF;
import org.megastage.util.Mapper;
import org.megastage.util.Time;

public class ForceFieldSystem extends EntitySystem {
    private long interval;

    private long wakeup;
    private double delta;

    public ForceFieldSystem() {
        super(Aspect.getAspectForAll(VirtualForceField.class));
    }

    public ForceFieldSystem(long interval) {
        this();
        this.interval = interval;
    }

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
        for (int i = 0, s = entities.size; s > i; i++) {
            process(entities.get(i));
        }
    }

    protected void process(Entity e) {
        VirtualForceField vff = Mapper.VIRTUAL_FORCE_FIELD.get(e);

        double r = vff.getRadius();
        if(r != vff.radius) {
            vff.dirty = true;
            vff.radius = r;
            
            Mapper.COLLISION_SPHERE.get(e).radius = r;
 
            if(r == 0.0) {
                vff.status = STATUS_POWER_OFF;
            } else if(r < 5.0) {
                vff.status = STATUS_FIELD_FORMING;
            } else {
                vff.status = STATUS_FIELD_ACTIVE;
            }
        }
    }
}
