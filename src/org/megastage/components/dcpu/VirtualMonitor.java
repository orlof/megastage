package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.Element;
import org.megastage.util.RAM;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class VirtualMonitor extends DCPUHardware {
    private final static Logger LOG = Logger.getLogger(VirtualMonitor.class.getName());

    public static final int WIDTH_CHARS = 32;
    public static final int HEIGHT_CHARS = 12;
    public static final int WIDTH_PIXELS = 128;
    public static final int HEIGHT_PIXELS = 96;

    public static char[] EMPTY = new char[384];
    public static char[] defaultFont = defaultFont();
    public static char[] defaultPalette = defaultPalette();

    public char videoRAMAddr = 0x8000;
    public char fontRAMAddr = 0x0000;
    public char paletteRAMAddr = 0x0000;
    public RAM videoRAM, fontRAM, paletteRAM;

    @Override
    public void init(World world, Entity parent, Element element) {
        type = TYPE_LEM;
        revision = 0x1802;
        manufactorer = MANUFACTORER_NYA_ELEKTRISKA;

        super.init(world, parent, element);

        videoRAM = new RAM();
        fontRAM = new RAM(defaultFont);
        paletteRAM = new RAM(defaultPalette);
    }

    public static char[] defaultPalette() {
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

    public static char[] defaultFont() {
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


    public void interrupt() {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        LOG.fine("a=" + Integer.toHexString(dcpu.registers[0]) + ", b=" + Integer.toHexString(dcpu.registers[1]));

        if (a == 0) {
            LOG.finer("Changed video ram address");
            videoRAMAddr = b;
        } else if (a == 1) {
            fontRAMAddr = b;
        } else if (a == 2) {
            paletteRAMAddr = b;
        } else if (a == 3) {
//            borderColor = (dcpu.registers[1] & 0xF);
        } else if (a == 4) {
            // dump font
            int offs = dcpu.registers[1];
            for (int i = 0; i < defaultFont.length; i++) {
                dcpu.ram[(offs + i & 0xFFFF)] = defaultFont[i];
            }
            dcpu.cycles += 256;
        } else if (a == 5) {
            // dump palette
            int offs = dcpu.registers[1];
            for (int i = 0; i < defaultPalette.length; i++) {
                dcpu.ram[(offs + i & 0xFFFF)] = defaultPalette[i];
            }
            dcpu.cycles += 16;
        }
    }
}
