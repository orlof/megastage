package org.megastage.systems.srv;

import org.megastage.ecs.World;
import org.megastage.ecs.Processor;
import org.megastage.components.srv.CollisionSphere;
import org.megastage.components.dcpu.VirtualForceField;
import org.megastage.ecs.CompType;

public class ForceFieldSystem extends Processor {
    public ForceFieldSystem(World world, long interval) {
        super(world, interval, CompType.VirtualForceField);
    }

    @Override
    protected void process(int eid) {
        VirtualForceField vff = (VirtualForceField) world.getComponent(eid, CompType.VirtualForceField);
        CollisionSphere cs = (CollisionSphere) world.getComponent(eid, CompType.CollisionSphere);
        cs.radius = vff.radius;
    }
}
