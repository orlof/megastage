package org.megastage.components.transfer;

import com.artemis.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.client.ClientVideoMemory;
import org.megastage.components.dcpu.LEMUtil;
import org.megastage.client.ClientGlobals;
import org.megastage.components.BaseComponent;
import org.megastage.protocol.Message;
import org.megastage.util.ID;
import org.megastage.util.RAM;

public class MonitorData extends BaseComponent {
    public char videoAddr = 0x8000;
    public RAM video = new RAM(LEMUtil.defaultVideo);

    public char fontAddr = 0x0000;
    public RAM font = new RAM(LEMUtil.defaultFont);

    public char paletteAddr = 0x0000;
    public RAM palette = new RAM(LEMUtil.defaultPalette);

    @Override
    public void receive(Connection pc, Entity entity) {
//        Log.info(ID.get(entity));
//        Log.info("video   [" + ((int) videoAddr) + "] " + video.toString());
//        Log.info("font    [" + ((int) fontAddr) + "] " + font.toString());
//        Log.info("palette [" + ((int) paletteAddr) + "] " + palette.toString());
        ClientVideoMemory videoMemory = ClientGlobals.artemis.getComponent(entity, ClientVideoMemory.class);
        videoMemory.update(this);
    }
}
