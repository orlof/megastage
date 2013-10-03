package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.RAM;

public class VirtualMonitor extends DCPUHardware {
    public char videoAddr = 0x8000;
    public RAM video = new RAM(LEMUtil.defaultVideo);

    public char fontAddr = 0x0000;
    public RAM font = new RAM(LEMUtil.defaultFont);

    public char paletteAddr = 0x0000;
    public RAM palette = new RAM(LEMUtil.defaultPalette);

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_LEM;
        revision = 0x1802;
        manufactorer = MANUFACTORER_NYA_ELEKTRISKA;

        super.init(world, parent, element);
    }

    public void interrupt() {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        if (a == 0) {
            videoAddr = b;
        } else if (a == 1) {
            fontAddr = b;
        } else if (a == 2) {
            paletteAddr = b;
        } else if (a == 3) {
//            borderColor = (dcpu.registers[1] & 0xF);
        } else if (a == 4) {
            // dump font
            int offs = dcpu.registers[1];
            for (int i = 0; i < LEMUtil.defaultFont.length; i++) {
                dcpu.ram[(offs + i & 0xFFFF)] = LEMUtil.defaultFont[i];
            }
            dcpu.cycles += 256;
        } else if (a == 5) {
            // dump palette
            int offs = dcpu.registers[1];
            for (int i = 0; i < LEMUtil.defaultPalette.length; i++) {
                dcpu.ram[(offs + i & 0xFFFF)] = LEMUtil.defaultPalette[i];
            }
            dcpu.cycles += 16;
        }
    }
}
