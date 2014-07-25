package org.megastage.components.dcpu;

import org.megastage.util.Log;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.Position;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class VirtualPPS extends DCPUHardware {
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_PPS, 0x6509, MANUFACTORER_TALON_NAVIGATION);
        
        return null;
    }

    @Override
    public boolean isDirty() {
        return false;
    }
    
    @Override
    public boolean isReplicable() {
        return false;
    }

    @Override
    public void interrupt(DCPU dcpu) {
        switch(dcpu.registers[0]) {
            case 0:
                if(getSectorNumber(dcpu)) {
                    // _
                } else {
                    // _
                }
                break;
            case 1:
                if(storeCoordinates(shipEID, dcpu, World.INSTANCE.time)) {
                    dcpu.cycles += 7;
                } else {
                    // _
                }
                break;
        }
    }
    
    private boolean storeCoordinates(int ship, DCPU dcpu, long time) {
        writeCoordinatesToMemory(time, dcpu.ram, dcpu.registers[1], ship);
        return true;
    }
    
    private boolean writeCoordinatesToMemory(long time, char[] mem, char ptr, int ship) {
        Position position = (Position) World.INSTANCE.getComponent(ship, CompType.Position);

        long x = Math.round(position.get().x / 100.0f); // 100m
        mem[ptr++] = (char) (x >> 16);
        mem[ptr++] = (char) x;

        long y = Math.round(position.get().y / 100.0f); // 100m
        mem[ptr++] = (char) (y >> 16);
        mem[ptr++] = (char) y;

        long z = Math.round(position.get().z / 100.0f); // 100m
        mem[ptr++] = (char) (z >> 16);
        mem[ptr++] = (char) z;

        mem[ptr++] = (char) time;
        
        return true;
    }

    private boolean getSectorNumber(DCPU dcpu) {
        Log.mark();
        dcpu.registers[1] = 0x0000;
        return true;
    }
}
