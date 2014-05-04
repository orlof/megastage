package org.megastage.components;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.ecs.World;

public class DeleteFlag extends BaseComponent {
    @Override
    public void receive(World world, Connection pc, int eid) {
        for(Object c=world.components(eid); c != null; c = world.nextComponent()) {
            if(c instanceof BaseComponent) {
                BaseComponent bc = (BaseComponent) c;
                bc.delete(world, pc, eid);
            }
        }
        world.deleteEntity(eid);
    }
}
