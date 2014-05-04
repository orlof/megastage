package org.megastage.protocol;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.World;

public class PlayerIDMessage extends EventMessage {
    private int eid = 0;
    private long ctime = System.currentTimeMillis();

    public PlayerIDMessage() {}
    public PlayerIDMessage(int id) {
        this();
        this.eid = id;
    }

    @Override
    public void receive(World world, Connection pc) {
        long time = System.currentTimeMillis();
        ClientGlobals.timeDiff = ctime - time;
        
        ClientGlobals.playerEntity = eid;
        ClientGlobals.spatialManager.setupPlayer(eid);
    }

    public String toString() {
        return "LoginResponse(" + eid + ")";
    }
}

