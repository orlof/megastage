package org.megastage.components;

import org.megastage.client.ClientGlobals;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.protocol.CharacterMode;

public class Mode extends ReplicatedComponent {
    public CharacterMode value = CharacterMode.WALK; 

    public void setMode(CharacterMode newValue) {
        if(value != newValue) {
            value = newValue;
            dirty = true;
        }
    }

    @Override
    public void receive(int eid) {
        super.receive(eid);
        
        if(ClientGlobals.playerEntity == eid) {
            CharacterMode.change(value);
        }
    }
}
