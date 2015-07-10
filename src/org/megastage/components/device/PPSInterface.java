package org.megastage.components.device;

import com.jme3.math.Vector3f;
import org.jdom2.Element;
import org.megastage.components.dcpu.DCPU;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class PPSInterface extends DCPUInterface {

    @Override
    public void config(Element elem) {
        setInfo(TYPE_PPS, 0x6509, MANUFACTORER_TALON_NAVIGATION);
    }

    @Override
    public void interrupt(DCPU dcpu, int eid) {
        switch(dcpu.registers[0]) {
            case 0:
                dcpu.registers[1] = 0x0000;
                break;
            case 1:
                Vector3f pos = getDevice(eid).getPosition(eid);

                if(writeCoordinatesToMemory(dcpu.ram, dcpu.registers[1], World.INSTANCE.time, pos)) {
                    dcpu.cycles += 7;
                } else {
                    // _
                }
                break;
        }
    }

    private PPSDevice getDevice(int eid) {
        return (PPSDevice) World.INSTANCE.getComponent(eid, CompType.PPSDevice);
    }

    private boolean writeCoordinatesToMemory(char[] mem, char ptr, long time, Vector3f pos) {
        long x = Math.round(pos.getX() / 100.0f); // 100m
        mem[ptr++] = (char) (x >> 16);
        mem[ptr++] = (char) x;

        long y = Math.round(pos.getY() / 100.0f); // 100m
        mem[ptr++] = (char) (y >> 16);
        mem[ptr++] = (char) y;

        long z = Math.round(pos.getZ() / 100.0f); // 100m
        mem[ptr++] = (char) (z >> 16);
        mem[ptr++] = (char) z;

        mem[ptr++] = (char) time;
        
        return true;
    }
}
