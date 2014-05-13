package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.ecs.World;

public interface Message {
    public void receive(World world, Connection pc);
}
