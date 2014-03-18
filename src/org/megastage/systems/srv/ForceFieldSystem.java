package org.megastage.systems.srv;

import com.artemis.Aspect;
import com.artemis.Entity;
import org.megastage.components.dcpu.VirtualForceField;
import org.megastage.util.Mapper;

public class ForceFieldSystem extends SystemTemplate {
    public ForceFieldSystem() {
        super(Aspect.getAspectForAll(VirtualForceField.class));
    }

    @Override
    protected void process(Entity e) {
        VirtualForceField vff = Mapper.VIRTUAL_FORCE_FIELD.get(e);
        vff.setEnergy(e, vff.energy * 0.995 + vff.power);
    }
}
