/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.util.ClientGlobals;

public class LoginResponse extends EventMessage {
    private int id = 0;

    public LoginResponse() {}
    public LoginResponse(int id) {
        this();
        this.id = id;
    }

    @Override
    public void receive(ClientNetworkSystem system, Connection pc) {
        Entity entity = system.cems.get(id);
        ClientGlobals.playerEntity = entity;
        system.csms.setupPlayer(entity);
    }

    public String toString() {
        return "LoginResponse(" + id + ")";
    }
}

