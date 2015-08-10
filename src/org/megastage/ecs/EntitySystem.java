package org.megastage.ecs;

public abstract class EntitySystem extends BaseSystem {
    protected Group group;

    public EntitySystem(World world, long interval, int... components) {
        super(world, interval);
        this.group = world.createGroup(components);
    }
    
    protected void processSystem() {
        for (int eid = group.iterator(); eid != 0; eid = group.next()) {
            processEntity(eid);
        }
    }

    protected abstract void processEntity(int eid) throws ECSException;
}
