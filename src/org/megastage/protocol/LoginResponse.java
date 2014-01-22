/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.protocol;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.client.ClientGlobals;

public class LoginResponse extends EventMessage {
    private int id = 0;
    private long time = 0;

    public LoginResponse() {}
    public LoginResponse(int id) {
        this();
        this.id = id;
    }

    @Override
    public void receive(Connection pc) {
        Entity entity = ClientGlobals.artemis.toClientEntity(id);
        ClientGlobals.playerEntity = entity;
        ClientGlobals.spatialManager.setupPlayer(entity);
    }

    public String toString() {
        return "LoginResponse(" + id + ")";
    }
}

