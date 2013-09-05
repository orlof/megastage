package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.Element;
import org.megastage.util.RAM;

public class VirtualMonitor extends DCPUHardware {
    public static final int WIDTH_CHARS = 32;
    public static final int HEIGHT_CHARS = 12;
    public static final int WIDTH_PIXELS = 128;
    public static final int HEIGHT_PIXELS = 96;

    public static char[] EMPTY = new char[384];

    public char videoRAMAddr = 0x8000;
    public RAM videoRAM, fontRAM, paletteRAM;

    @Override
    public void init(World world, Entity parent, Element element) {
        type = TYPE_LEM;
        revision = 0x1802;
        manufactorer = MANUFACTORER_NYA_ELEKTRISKA;

        super.init(world, parent, element);

        videoRAM = new RAM();
    }

    public void interrupt() {
        System.out.println("VirtualMonitor.interrupt");
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        if (a == 0) {
            videoRAMAddr = b;
            System.out.println("updated video ram address b = " + ((int) b));
        } else if (a == 1) {
            //           fontMemMap = dcpu.registers[1];
        } else if (a == 2) {
//            paletteMemMap = dcpu.registers[1];
        } else if (a == 3) {
//            borderColor = (dcpu.registers[1] & 0xF);
        } else if (a == 4) {
            // dump font
            int offs = dcpu.registers[1];
//            for (int i = 0; i < font.length; i++) {
//                dcpu.ram[(offs + i & 0xFFFF)] = font[i];
//            }
            dcpu.cycles += 256;
        } else if (a == 5) {
            // dump palette
            int offs = dcpu.registers[1];
            for (int i = 0; i < 16; i++) {
                int blue = (i >> 0 & 0x1) * 10;
                int green = (i >> 1 & 0x1) * 10;
                int red = (i >> 2 & 0x1) * 10;
                if (i == 6) {
                    green -= 5;
                } else if (i >= 8) {
                    red += 5;
                    green += 5;
                    blue += 5;
                }
                dcpu.ram[(offs + i & 0xFFFF)] = (char) (red << 8 | green << 4 | blue);
            }
            dcpu.cycles += 16;
        }
    }
}
