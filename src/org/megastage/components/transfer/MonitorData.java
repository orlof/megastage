package org.megastage.components.transfer;

import com.esotericsoftware.kryonet.Connection;
import org.megastage.components.client.ClientVideoMemory;
import org.megastage.components.dcpu.LEMUtil;
import org.megastage.components.BaseComponent;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.util.RAM;

public class MonitorData extends BaseComponent {
    public char videoAddr = 0x8000;
    public RAM video = new RAM(LEMUtil.defaultVideo);

    public char fontAddr = 0x0000;
    public RAM font = new RAM(LEMUtil.defaultFont);

    public char paletteAddr = 0x0000;
    public RAM palette = new RAM(LEMUtil.defaultPalette);

    @Override
    public void receive(World world, Connection pc, int eid) {
//        Log.info(ID.get(eid));
//        Log.info("video   [" + ((int) videoAddr) + "] " + video.toString());
//        Log.info("font    [" + ((int) fontAddr) + "] " + font.toString());
//        Log.info("palette [" + ((int) paletteAddr) + "] " + palette.toString());
        ClientVideoMemory videoMemory = (ClientVideoMemory) world.getComponent(eid, CompType.ClientVideoMemory);
        videoMemory.update(this);
    }
}
