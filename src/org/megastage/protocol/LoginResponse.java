/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.util.ClientGlobals;
import org.megastage.util.Globals;

public class LoginResponse extends EventMessage {
    private int id = 0;
    private long time = 0;

    public LoginResponse() {}
    public LoginResponse(int id) {
        this();
        this.id = id;
        this.time = Globals.time;
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc) {
        ClientGlobals.timeDiff += time - System.currentTimeMillis();
        Log.info("Timediff: " + ClientGlobals.timeDiff);
        Entity entity = system.cems.get(id);
        ClientGlobals.playerEntity = entity;
        system.csms.setupPlayer(entity);
    }

    public String toString() {
        return "LoginResponse(" + id + ")";
    }
}

