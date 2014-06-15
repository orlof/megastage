package org.megastage.components.gfx;

import org.megastage.client.ClientGlobals;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
    
public class GeometryComponent extends ReplicatedComponent {
    @Override
    public void receive(int eid) {
        assert !World.INSTANCE.hasComponent(eid, getClass());

        super.receive(eid);
        ClientGlobals.spatialManager.setupGeometry(eid, this);
    }

    @Override
    public void delete(int eid) {
        ClientGlobals.spatialManager.deleteEntity(eid);
    }
}
