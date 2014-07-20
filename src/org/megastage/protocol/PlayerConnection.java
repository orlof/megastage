package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;

public class PlayerConnection extends Connection {
    public int player;
    public int item;
    public boolean isInitialized;
}
