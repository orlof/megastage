package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.Position;
import org.megastage.util.Time;

public class PPS extends DCPUHardware {
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_PPS;
        revision = 0x2222;
        manufactorer = MANUFACTORER_TALON_NAVIGATION;

        super.init(world, parent, element);
        
        return null;
    }

    public void interrupt() {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        Log.debug("a=" + Integer.toHexString(a) + ", b=" + Integer.toHexString(b));

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

            dcpu.ram[b++ & 0xffff] = (char) (Time.value >> 48 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (Time.value >> 32 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (Time.value >> 16 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (Time.value >> 00 & 0xffff);

            dcpu.cycles += 16;
        }
    }

}
