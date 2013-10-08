package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.Position;
import org.megastage.util.Globals;

import java.util.logging.Logger;

public class PPS extends DCPUHardware {
    private final static Logger LOG = Logger.getLogger(PPS.class.getName());

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_PPS;
        revision = 0x2222;
        manufactorer = MANUFACTORER_TALON_NAVIGATION;

        super.init(world, parent, element);
    }

    public void interrupt() {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        LOG.fine("a=" + Integer.toHexString(a) + ", b=" + Integer.toHexString(b));

        if (a == 0) {
            Position position = ship.getComponent(Position.class);

            dcpu.ram[b++ & 0xffff] = (char) (position.x >> 48 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (position.x >> 32 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (position.x >> 16 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (position.x >> 00 & 0xffff);

            dcpu.ram[b++ & 0xffff] = (char) (position.y >> 48 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (position.y >> 32 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (position.y >> 16 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (position.y >> 00 & 0xffff);

            dcpu.ram[b++ & 0xffff] = (char) (position.z >> 48 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (position.z >> 32 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (position.z >> 16 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (position.z >> 00 & 0xffff);

            dcpu.ram[b++ & 0xffff] = (char) (Globals.time >> 48 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (Globals.time >> 32 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (Globals.time >> 16 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (Globals.time >> 00 & 0xffff);

            dcpu.cycles += 16;
        }
    }

}
