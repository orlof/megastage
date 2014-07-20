package org.megastage.components;

import org.megastage.client.ClientGlobals;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.protocol.CharacterMode;

public class Mode extends ReplicatedComponent {
    public int value = CharacterMode.WALK; 

    public void setMode(int mode) {
        this.value = mode;
        dirty = true;
    }

    @Override
    public void receive(int eid) {
        if(ClientGlobals.playerEntity == eid) {
            ClientGlobals.cmdHandler.changeMode(value);
        }
    }
}
