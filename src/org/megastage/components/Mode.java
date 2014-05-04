package org.megastage.components;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.World;
import org.megastage.protocol.CharacterMode;
import org.megastage.protocol.Message;

public class Mode extends BaseComponent {
    public int value = CharacterMode.WALK; 

    @Override
    public Message replicate(int eid) {
        return always(eid);
    }
    
    @Override
    public Message synchronize(int eid) {
        return ifDirty(eid);
    }

    public void setMode(int mode) {
        this.value = mode;
        dirty = true;
    }

    @Override
    public void receive(World world, Connection pc, int eid) {
        if(ClientGlobals.playerEntity == eid) {
            ClientGlobals.cmdHandler.changeMode(value);
        }
    }

    public String toString() {
        return "Mode(mode=" + value + ")";
    }
}
