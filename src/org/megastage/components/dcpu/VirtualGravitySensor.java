package org.megastage.components.dcpu;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.Mass;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;
import org.megastage.server.GravityField;
import org.megastage.server.SoiData;
import org.megastage.systems.srv.GravityManagerSystem;
import org.megastage.systems.srv.SoiManagerSystem;
import org.megastage.util.Bag;
import org.megastage.util.Globals;

public class VirtualGravitySensor extends DCPUHardware {

    // COMPONENT
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_GRAVITATION_SENSOR, 0x8668, MANUFACTORER_OTEC);
        
        return null;
    }

    // DCPU
    
    @Override
    public void interrupt(DCPU dcpu) {
        switch(dcpu.registers[0]) {
            case 0:
                if(storeFieldSignatures(shipEID, dcpu)) {
                    dcpu.cycles += 16;
                } 
                break;
            case 1:
                if(storeOrbitalStateVector(shipEID, dcpu, false)) {
                    dcpu.cycles += 12;
                    dcpu.registers[2] = 0xffff;
                } else {
                    dcpu.registers[2] = 0x0000;
                }
                break;
            case 2:
                if(storeFieldData(shipEID, dcpu)) {
                    dcpu.cycles += 4;
                    dcpu.registers[2] = 0xffff;
                } else {
                    dcpu.registers[2] = 0x0000;
                }
                break;
            case 3:
                getSOISignature(shipEID, dcpu);
                break;
            case 4:
                if(storeOrbitalStateVector(shipEID, dcpu, true)) {
                    dcpu.cycles += 12;
                    dcpu.registers[2] = 0xffff;
                } else {
                    dcpu.registers[2] = 0x0000;
                } 
                break;
        }
    }

    // INTERRUPTS
    
    private boolean storeFieldSignatures(int ship, DCPU dcpu) {
        Bag<GravityField> fields = GravityManagerSystem.INSTANCE.getGravityFields(ship, true);
        writeFieldSignaturesToMemory(dcpu.ram, dcpu.registers[1], fields, 16);
        return true;
    }
    
    private boolean storeOrbitalStateVector(int ship, DCPU dcpu, boolean ieee754) {
        int reference = GravityManagerSystem.INSTANCE.findBySignature(dcpu.registers[1]);
        if(reference == 0) return false;
        
        GravityManagerSystem.INSTANCE.writeOrbitalStateVectorToMemory(dcpu.ram, dcpu.registers[2], reference, ship, ieee754);
        return true;
    }

    private boolean storeFieldData(int ship, DCPU dcpu) {
        int field = GravityManagerSystem.INSTANCE.findBySignature(dcpu.registers[1]);
        if(field == 0) return false;

        writeFieldDataToMemory(ship, dcpu.ram, dcpu.registers[1], field);
        return true;
    }

    private boolean getSOISignature(int ship, DCPU dcpu) {
        SoiData soi = SoiManagerSystem.INSTANCE.getSoi(ship);
        if(soi != null) {
            dcpu.registers[1] = (char) (soi.eid & 0xffff);
        } else {
            dcpu.registers[1] = 0;            
        }
        return true;
    }

    // UTILS
    
    private void writeFieldSignaturesToMemory(char[] mem, char ptr, Bag<GravityField> fields, int maxNum) {
        for(int i = 0; i < maxNum; i++) {
            if(i < fields.size()) {
                mem[ptr++] = (char) (fields.get(i).eid & 0xffff);
            } else {
                mem[ptr++] = 0;
            }
        }
    }

    private void writeFieldDataToMemory(int ship, char[] mem, char ptr, int field) {
        // target mass
        Mass mass = (Mass) World.INSTANCE.getComponent(field, CompType.Mass);
        float sgp = (float) (mass.value * Globals.G);
        ptr = writeFloatToMemory(mem, ptr, sgp);
        ptr = writePitchAndYawToMemory(mem, ptr, ship, field);
    }
    
}