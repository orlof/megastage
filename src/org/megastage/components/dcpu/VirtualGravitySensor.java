package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.server.GravityManager;
import org.megastage.server.GravityManager.GravityField;
import org.megastage.server.SOIManager;
import org.megastage.server.SOIManager.SOIData;
import org.megastage.util.Globals;
import org.megastage.util.Mapper;
import org.megastage.util.Vector3d;

public class VirtualGravitySensor extends DCPUHardware {

    // COMPONENT
    
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_GRAVITATION_SENSOR;
        revision = 0x8668;
        manufactorer = MANUFACTORER_OTEC;

        super.init(world, parent, element);
        
        return null;
    }

    // DCPU
    
    @Override
    public void interrupt() {
        switch(dcpu.registers[0]) {
            case 0:
                if(storeFieldSignatures()) {
                    dcpu.cycles += 16;
                } 
                break;
            case 1:
                if(storeOrbitalStateVector(false)) {
                    dcpu.cycles += 12;
                    dcpu.registers[2] = 0xffff;
                } else {
                    dcpu.registers[2] = 0x0000;
                }
                break;
            case 2:
                if(storeFieldData()) {
                    dcpu.cycles += 4;
                    dcpu.registers[2] = 0xffff;
                } else {
                    dcpu.registers[2] = 0x0000;
                }
                break;
            case 3:
                getSOISignature();
                break;
            case 4:
                if(storeOrbitalStateVector(true)) {
                    dcpu.cycles += 12;
                    dcpu.registers[2] = 0xffff;
                } else {
                    dcpu.registers[2] = 0x0000;
                } 
                break;
        }
    }

    @Override
    public void tick60hz() {
    }

    // INTERRUPTS
    
    private boolean storeFieldSignatures() {
        Array<GravityField> fields = GravityManager.getGravityFields(ship, true);
        writeFieldSignaturesToMemory(dcpu.ram, dcpu.registers[1], fields, 16);
        return true;
    }
    
    private boolean storeOrbitalStateVector(boolean ieee754) {
        Entity reference = GravityManager.findBySignature(dcpu.registers[1]);
        if(reference == null) return false;
        
        GravityManager.writeOrbitalStateVectorToMemory(dcpu.ram, dcpu.registers[2], reference, ship, ieee754);
        return true;
    }

    private boolean storeFieldData() {
        Entity field = GravityManager.findBySignature(dcpu.registers[1]);
        if(field == null) return false;

        writeFieldDataToMemory(dcpu.ram, dcpu.registers[1], field);
        return true;
    }

    private boolean getSOISignature() {
        Vector3d ownCoord = Mapper.POSITION.get(ship).getVector3d();
        SOIData soi = SOIManager.getSOI(ownCoord);
        if(soi != null) {
            dcpu.registers[1] = (char) (soi.entity.id & 0xffff);
        } else {
            dcpu.registers[1] = 0;            
        }
        return true;
    }

    // UTILS
    
    private void writeFieldSignaturesToMemory(char[] mem, char ptr, Array<GravityField> fields, int maxNum) {
        for(int i = 0; i < maxNum; i++) {
            if(i < fields.size) {
                mem[ptr++] = (char) (fields.get(i).entity.id & 0xffff);
            } else {
                mem[ptr++] = 0;
            }
        }
    }

    private void writeFieldDataToMemory(char[] mem, char ptr, Entity field) {
        // target mass
        float sgp = (float) (Mapper.MASS.get(field).mass * Globals.G);
        ptr = writeFloatToMemory(mem, ptr, sgp);
        ptr = writePitchAndYawToMemory(mem, ptr, ship, field);
    }
    

    
}