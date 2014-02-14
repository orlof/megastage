package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.Position;
import org.megastage.util.Mapper;
import org.megastage.util.Time;

public class PPS extends DCPUHardware {
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_PPS;
        revision = 0x6509;
        manufactorer = MANUFACTORER_TALON_NAVIGATION;

        super.init(world, parent, element);
        
        return null;
    }

    public void interrupt() {
        char a = dcpu.registers[0];
        char b = dcpu.registers[1];

        if (a == 0) {
            dcpu.registers[1] = (char) 0;
        } else if(a == 1) {
            Position position = Mapper.POSITION.get(ship);

            long x = position.x / 100000; // 100m
            dcpu.ram[b++ & 0xffff] = (char) (x >> 16 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (x >> 00 & 0xffff);

            long y = position.y / 100000; // 100m
            dcpu.ram[b++ & 0xffff] = (char) (y >> 16 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (y >> 00 & 0xffff);

            long z = position.z / 100000; // 100m
            dcpu.ram[b++ & 0xffff] = (char) (z >> 16 & 0xffff);
            dcpu.ram[b++ & 0xffff] = (char) (z >> 00 & 0xffff);

            dcpu.ram[b++ & 0xffff] = (char) (Time.value & 0xffff);

            dcpu.cycles += 7;
        }
    }

}
