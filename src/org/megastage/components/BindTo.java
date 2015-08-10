package org.megastage.components;

import org.megastage.client.ClientGlobals;
import org.megastage.client.EntityNode;
import org.megastage.client.SpatialManager;
import org.megastage.components.generic.EntityReference;
import org.megastage.ecs.CompType;
import org.megastage.ecs.DirtyComponent;
import org.megastage.ecs.World;

public class BindTo extends EntityReference {
    @Override
    public void receive(int eid) {
        World.INSTANCE.setComponent(eid, CompType.BindTo, this);

        EntityNode parentNode = SpatialManager.getOrCreateNode(this.eid);
        EntityNode childNode = SpatialManager.getOrCreateNode(eid);
        parentNode.offset.attachChild(childNode);

        if(eid == ClientGlobals.playerEntity) {
            ClientGlobals.setBase(eid);
        }
    }
}
