package org.megastage.components;

import org.megastage.client.ClientGlobals;
import org.megastage.components.gfx.CharacterGeometry;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;

public class CmdText extends ReplicatedComponent {
    public String text;

    public static CmdText create(String text) {
        CmdText me = new CmdText();
        me.text = text;
        return me;
    }

    @Override
    public void receive(int eid) {
        for(int i=0; i < ClientGlobals.chatLabel.length-1; i++) {
            String next = ClientGlobals.chatLabel[i+1].getText();
            ClientGlobals.chatLabel[i].setText(next);
        }
        CharacterGeometry cg = (CharacterGeometry) World.INSTANCE.getComponent(eid, CompType.CharacterGeometry);
        
        String source = "Unknown";
        if(cg != null) {
            source = cg.name;
        }

        String msg = String.format("%s: %s", source, text);
        ClientGlobals.chatLabel[ClientGlobals.chatLabel.length-1].setText(msg);
    }
}
