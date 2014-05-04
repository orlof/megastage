package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.ecs.World;

public abstract class EventMessage implements Message {
    @Override
    public void receive(World world, Connection pc) {}
}
    
