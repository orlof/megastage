package org.megastage.components.client;

import com.artemis.Component;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.ColorRGBA;
import org.megastage.components.MonitorData;
import org.megastage.components.dcpu.LEMUtil;

/**
 * MegaStage
 * User: Orlof
 * Date: 1.9.2013
 * Time: 21:48                                                      0
 */
public final class ClientVideoMemory extends Component {
    // public BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
    // public int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

    public char[] screenMemRam = new char[384];
    public char[] fontMemRam = new char[256];
    //public int[] paletteMemRam = new int[16];
    public ColorRGBA[] colors = new ColorRGBA[16];

    public boolean blink = false;
    public boolean isDirty = false;

    public ClientVideoMemory() {
        updatePalette(LEMUtil.defaultPalette);
    }
    
    public void updatePalette(char[] mem) {
        colors = getColorArray(mem);
        isDirty = true;
    }

    public static ColorRGBA[] getColorArray(char[] mem) {
        ColorRGBA[] colors = new ColorRGBA[16];
        for (int i = 0; i < 16; i++) {
            char ch = mem[i];
            int b = (ch & 0xF) * 17;
            int g = (ch >> 4 & 0xF) * 17;
            int r = (ch >> 8 & 0xF) * 17;

            int argb = (0xFF000000 | r << 16 | g << 8 | b);
            colors[i] = new ColorRGBA();
            colors[i].fromIntARGB(argb);

        }
        return colors;
    }
    
    public void updateVideo(char[] mem) {
        screenMemRam = mem;
        isDirty = true;
    }

    public void updateFont(char[] mem) {
        fontMemRam = mem;
        isDirty = true;
    }

    public void update(MonitorData data) {
        Log.info("video   [" + ((int) data.videoAddr) + "] " + data.video.toString());
        Log.info("font    [" + ((int) data.fontAddr) + "] " + data.font.toString());
        Log.info("palette [" + ((int) data.paletteAddr) + "] " + data.palette.toString());

        updateVideo(data.videoAddr == 0 ? null: data.video.mem);
        updateFont(data.font.mem);
        updatePalette(data.palette.mem);
    }
}
