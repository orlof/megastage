package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntitySystem;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.CollisionSphere;
import org.megastage.components.dcpu.VirtualForceField;
import static org.megastage.components.dcpu.VirtualForceField.STATUS_FIELD_ACTIVE;
import static org.megastage.components.dcpu.VirtualForceField.STATUS_FIELD_FORMING;
import static org.megastage.components.dcpu.VirtualForceField.STATUS_POWER_OFF;
import org.megastage.util.Mapper;
import org.megastage.util.GlobalTime;

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

    protected void process(Entity e) {
        VirtualForceField vff = Mapper.VIRTUAL_FORCE_FIELD.get(e);
        Mapper.COLLISION_SPHERE.get(e).radius = vff.radius;
    }
}
