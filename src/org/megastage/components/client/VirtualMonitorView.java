package org.megastage.components.client;

import com.artemis.Component;
import org.megastage.util.RAM;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

/**
 * MegaStage
 * User: Orlof
 * Date: 1.9.2013
 * Time: 21:48                                                      0
 */
public class VirtualMonitorView extends Component {
    public BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
    // public int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

    public char[] screenMemRam = new char[384];
    public char[] fontMemRam = new char[256];
    public int[] paletteMemRam = new int[16];

    public boolean blink = false;
    public boolean isDirty = false;

    public VirtualMonitorView() {
        resetFont();
        resetPalette();
    }

    public void resetFont() {
        int[] pixels = new int[4096];
        try {
            ImageIO.read(new File("font.png")).getRGB(0, 0, 128, 32, pixels, 0, 128);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int c = 0; c < 128; c++) {
            int ro = c * 2;
            int xo = c % 32 * 4;
            int yo = c / 32 * 8;
            for (int x = 0; x < 4; x++) {
                int bb = 0;
                for (int y = 0; y < 8; y++)
                    if ((pixels[(xo + x + (yo + y) * 128)] & 0xFF) > 128)
                        bb |= 1 << y;
                fontMemRam[ro + x / 2] = (char) (fontMemRam[ro + x / 2] | bb << (x + 1 & 0x1) * 8);
            }
        }
    }

    public void resetPalette() {
        for (int i = 0; i < 16; i++) {
            int b = (i >> 0 & 0x1) * 170;
            int g = (i >> 1 & 0x1) * 170;
            int r = (i >> 2 & 0x1) * 170;
            if (i == 6) {
                g -= 85;
            } else if (i >= 8) {
                r += 85;
                g += 85;
                b += 85;
            }
            paletteMemRam[i] = 0xff000000 | r << 16 | g << 8 | b;
        }
    }

    public void updatePalette(RAM ram) {
        for (int i = 0; i < 16; i++) {
            char ch = ram.mem[i];
            int b = (ch & 0xF) * 17;
            int g = (ch >> 4 & 0xF) * 17;
            int r = (ch >> 8 & 0xF) * 17;
            paletteMemRam[i] = (0xFF000000 | r << 16 | g << 8 | b);
        }
    }


    public void updateVideo(char[] mem) {
        screenMemRam = mem;
        isDirty = true;
    }



}
