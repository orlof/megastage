package org.megastage.components;

import org.megastage.client.EntityNode;
import org.megastage.client.ExplosionNode;
import org.megastage.client.SpatialManager;
import org.megastage.client.controls.ExplosionControl;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;

public class Explosion extends ReplicatedComponent {
    public transient long startTime = World.INSTANCE.time;

    public int state = -1;

    @Override
    public void receive(int eid) {
        if(!World.INSTANCE.hasComponent(eid, CompType.Explosion)) {
            ExplosionNode explosionNode = new ExplosionNode("ExplosionNode");
            explosionNode.addControl(new ExplosionControl(eid));

            EntityNode node = SpatialManager.getOrCreateNode(eid);
            node.offset.attachChild(explosionNode);
        }

        World.INSTANCE.setComponent(eid, this);
    }

    public void setState(int state) {
        if(this.state != state) {
            this.state = state;
            dirty = true;
        }
    }
}
