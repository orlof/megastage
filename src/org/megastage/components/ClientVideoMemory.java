package org.megastage.components;

import com.artemis.Component;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.plugins.AWTLoader;
import org.megastage.protocol.Network;
import java.awt.image.BufferedImage;

/**
 * MegaStage
 * User: Orlof
 * Date: 1.9.2013
 * Time: 21:48                                                      0
 */
public class ClientVideoMemory extends Component {
    // public BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
    // public int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

    public char[] screenMemRam = new char[384];
    public char[] fontMemRam = new char[256];
    public int[] paletteMemRam = new int[16];

    public boolean blink = false;
    public boolean isDirty = false;
    
    public void updatePalette(char[] mem) {
        for (int i = 0; i < 16; i++) {
            char ch = mem[i];
            int b = (ch & 0xF) * 17;
            int g = (ch >> 4 & 0xF) * 17;
            int r = (ch >> 8 & 0xF) * 17;
            paletteMemRam[i] = (0xFF000000 | r << 16 | g << 8 | b);
        }
        isDirty = true;
    }

    public void updateVideo(char[] mem) {
        screenMemRam = mem;
        isDirty = true;
    }

    public void updateFont(char[] mem) {
        fontMemRam = mem;
        isDirty = true;
    }


    public void update(Network.MonitorData monitorData) {
        updateVideo(monitorData.video);
        updateFont(monitorData.font);
        updatePalette(monitorData.palette);

    }
}
