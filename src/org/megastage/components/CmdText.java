package org.megastage.components;

import org.megastage.client.ClientGlobals;
import org.megastage.ecs.BaseComponent;

public class CmdText extends BaseComponent {
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

        ClientGlobals.chatLabel[ClientGlobals.chatLabel.length-1].setText(text);
    }
}
