package org.megastage.components.dcpu;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.Mass;
import org.megastage.components.Position;
import org.megastage.components.transfer.RadarTargetData;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.server.RadarSignal;
import org.megastage.server.SoiData;
import org.megastage.systems.srv.GravityManagerSystem;
import org.megastage.systems.srv.RadarManagerSystem;
import org.megastage.systems.srv.SoiManagerSystem;
import org.megastage.util.Bag;
import org.megastage.util.Vector3d;

public class VirtualRadar extends DCPUHardware {
    public int target = 0;

    // COMPONENT
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_RADAR, 0x0090, MANUFACTORER_ENDER_INNOVATIONS);
        
        return null;
    }

    @Override
    public Message synchronize(int eid) {
        return RadarTargetData.create(target).synchronize(eid);
    }
    
    // DCPU
    
    @Override
    public void interrupt(DCPU dcpu) {
        switch(dcpu.registers[0]) {
            case 0:
                if(storeRadarSignatures(shipEID, dcpu)) {
                    dcpu.cycles += 16;
                } else {
                }
                break;
            case 1:
                if(setTrackingTarget(dcpu)) {
                    dcpu.registers[2] = 0xffff;
                } else {
                    dcpu.registers[2] = 0x0000;
                }
                break;
            case 2:
                if(storeTargetData(shipEID, dcpu)) {
                    dcpu.registers[2] = 0xffff;
                    dcpu.cycles += 7;
                } else {
                    dcpu.registers[2] = 0x0000;
                }
                break;
            case 3:
                dcpu.registers[2] = storeOrbitalStateVector(shipEID, dcpu);
                if(dcpu.registers[2] == 0xffff) {
                    dcpu.cycles += 12;
                }
                break;
        }
    }

    // INTERRUPT ROUTINES
    
    private boolean storeRadarSignatures(int ship, DCPU dcpu) {
        Bag<RadarSignal> signals = RadarManagerSystem.INSTANCE.getRadarSignals(ship);

        writeSignalsToMemory(dcpu.ram, dcpu.registers[1], signals);
        return true;
    }

    private boolean setTrackingTarget(DCPU dcpu) {
        int eid = RadarManagerSystem.INSTANCE.findBySignature(dcpu.registers[1]);
        
        setTrackingTarget(eid);

        return eid != 0;
    }

    private boolean storeTargetData(int ship, DCPU dcpu) {
        if(target == 0) {
            return false;
        }

        writeTargetDataToMemory(dcpu.ram, dcpu.registers[1], ship, target);
        return true;
    }
     
    private char storeOrbitalStateVector(int ship, DCPU dcpu) {
        if(target == 0) {
            return 0x0001;
        }

        SoiData soi = SoiManagerSystem.INSTANCE.getSoi(ship);
        
        if(soi == null) {
            return 0x0002;
        }

        GravityManagerSystem.INSTANCE.writeOrbitalStateVectorToMemory(dcpu.ram, dcpu.registers[1], soi.eid, target, false);

        return 0xFFFF;
    }

    // UTILS
    
    public void writeSignalsToMemory(char[] mem, char ptr, Bag<RadarSignal> signals) {
        for(int i = 0; i < 16; i++) {
            if(i >= signals.size()) {
                mem[ptr++] = 0;
            } else {
                mem[ptr++] = (char) signals.get(i).eid;
            }
        }
    }

    public void setTrackingTarget(int eid) {
        if(target != eid) {
            //Log.info(ID.get(eid));

            target = eid;
            dirty = true;
        }
    }

    private void writeTargetDataToMemory(char[] mem, char ptr, int ship, int target) {
        // target type
        mem[ptr++] = 0x0002;

        // target mass
        Mass targetMass = (Mass) World.INSTANCE.getComponent(target, CompType.Mass);
        int mass = (int) targetMass.value;
        //Log.info("MASS: " + mass);

        mem[ptr++] = (char) ((mass >> 16) & 0xffff);
        mem[ptr++] = (char) (mass & 0xffff);

        // distance (float)
        Position shipPos = (Position) World.INSTANCE.getComponent(ship, CompType.Position);
        Position targetPos = (Position) World.INSTANCE.getComponent(target, CompType.Position);
        
        int distance = (int) Math.round(shipPos.get().distance(targetPos.get()));
        //Log.info("DISTANCE: " + distance);
        
        mem[ptr++] = (char) ((distance >> 16) & 0xffff);
        mem[ptr++] = (char) (distance & 0xffff);

        //ptr = writeFloatToMemory(mem, ptr, distance);

        ptr = writePitchAndYawToMemory(mem, ptr, ship, target);
    }
}
