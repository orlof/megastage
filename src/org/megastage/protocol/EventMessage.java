package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;

public abstract class EventMessage implements Message {
    @Override
    public void receive(Connection pc) {}
}
    
