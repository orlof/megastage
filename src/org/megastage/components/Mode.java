package org.megastage.components;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.protocol.CharacterMode;
import org.megastage.protocol.Message;

public class Mode extends BaseComponent {
    public int value = CharacterMode.WALK; 

    @Override
    public Message replicate(Entity entity) {
        return always(entity);
    }
    
    @Override
    public Message synchronize(Entity entity) {
        return ifDirty(entity);
    }

    public void setMode(int mode) {
        this.value = mode;
        dirty = true;
    }

    @Override
    public void receive(Connection pc, Entity entity) {
        if(ClientGlobals.playerEntity == entity) {
            ClientGlobals.cmdHandler.changeMode(value);
        }
    }

    public String toString() {
        return "Mode(mode=" + value + ")";
    }
}
