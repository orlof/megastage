/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.server;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.components.EntityComponent;
import org.megastage.protocol.CharacterMode;
import org.megastage.protocol.Network;

/**
 * This entity's position and rotation are relative to parent
 * @author Orlof
 */
public class Mode extends EntityComponent {
    public int mode = CharacterMode.WALK; 
    public boolean isDirty = true;

    @Override
    public boolean isUpdated() {
        return isDirty;
    }

    public void setMode(int mode) {
        this.mode = mode;
        isDirty = true;
    }

    @Override
    public Network.ComponentMessage create(Entity entity) {
        isDirty = false;
        return new Network.ComponentMessage(entity, this);
    }
    
    
    @Override
    public void receive(Connection pc, Entity entity) {
        if(ClientGlobals.playerEntity == entity) {
            ClientGlobals.cmdHandler.changeMode(mode);
        }
    }

    public String toString() {
        return "Mode(mode=" + mode + ")";
    }
}
