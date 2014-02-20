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

public class VirtualPPS extends DCPUHardware {
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_PPS;
        revision = 0x6509;
        manufactorer = MANUFACTORER_TALON_NAVIGATION;

        super.init(world, parent, element);
        
        return null;
    }

    public void interrupt() {
        Log.info(""+(int)dcpu.registers[0]);
        switch(dcpu.registers[0]) {
            case 0:
                if(getSectorNumber()) {
                    // _
                } else {
                    // _
                }
                break;
            case 1:
                if(storeCoordinates()) {
                    dcpu.cycles += 7;
                } else {
                    // _
                }
                break;
        }
    }
    
    private boolean storeCoordinates() {
        writeCoordinatesToMemory(dcpu.ram, dcpu.registers[1], ship);
        return true;
    }
    
    private boolean writeCoordinatesToMemory(char[] mem, char ptr, Entity ship) {
        Position position = Mapper.POSITION.get(ship);

        long x = position.x / 100000; // 100m
        mem[ptr++] = (char) (x >> 16);
        mem[ptr++] = (char) x;

        long y = position.y / 100000; // 100m
        mem[ptr++] = (char) (y >> 16);
        mem[ptr++] = (char) y;

        long z = position.z / 100000; // 100m
        mem[ptr++] = (char) (z >> 16);
        mem[ptr++] = (char) z;

        mem[ptr++] = (char) Time.value;
        
        return true;
    }

    private boolean getSectorNumber() {
        Log.info("");
        dcpu.registers[1] = 0x0000;
        return true;
    }
}
