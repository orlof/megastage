package org.megastage.components.dcpu;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class LEMUtil {
    public static char[] defaultVideo = new char[384];
    public static char[] defaultFont = defaultFont();
    public static char[] defaultPalette = defaultPalette();

    private static char[] defaultPalette() {
        char[] palette = new char[16];
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
            palette[i] = (char) ((r / 17) << 8 | (g / 17) << 4 | (b / 17));
        }
        return palette;
    }

    private static char[] defaultFont() {
        int[] pixels = new int[4096];
        char[] font = new char[256];
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
                font[ro + x / 2] = (char) (font[ro + x / 2] | bb << (x + 1 & 0x1) * 8);
            }
        }
        return font;
    }
}
