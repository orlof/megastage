package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;

public interface Message {
    public void receive(Connection pc);
}
