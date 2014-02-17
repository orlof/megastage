package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.transfer.RadarTargetData;
import org.megastage.protocol.Network;
import org.megastage.server.GravityManager;
import org.megastage.server.RadarManager;
import org.megastage.server.RadarManager.RadarSignal;
import org.megastage.server.SOIManager;
import org.megastage.server.SOIManager.SOIData;
import org.megastage.util.Mapper;
import org.megastage.util.Vector3d;

public class VirtualRadar extends DCPUHardware {
    public Entity target = null;
    private boolean dirty = false;

    // COMPONENT
    
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_RADAR;
        revision = 0x90;
        manufactorer = MANUFACTORER_ENDER_INNOVATIONS;

        super.init(world, parent, element);
        
        return null;
    }

    @Override
    public boolean replicate() {
        return target != null;
    }
    
    @Override
    public boolean synchronize() {
        return dirty;
    }

    @Override
    public Network.ComponentMessage create(Entity entity) {
        dirty = false;

        RadarTargetData data = new RadarTargetData();
        data.target = target.id;
        
        return data.create(entity);
    }

    // DCPU
    
    @Override
    public void interrupt() {
        switch(dcpu.registers[0]) {
            case 0:
                if(storeRadarSignatures()) {
                    dcpu.cycles += 16;
                } else {
                }
                break;
            case 1:
                if(setTrackingTarget()) {
                    dcpu.registers[2] = 0xffff;
                } else {
                    dcpu.registers[2] = 0x0000;
                }
                break;
            case 2:
                if(storeTargetData()) {
                    dcpu.registers[2] = 0xffff;
                    dcpu.cycles += 7;
                } else {
                    dcpu.registers[2] = 0x0000;
                }
                break;
            case 3:
                if(storeOrbitalStateVector()) {
                    dcpu.registers[2] = 0xffff;
                    dcpu.cycles += 12;
                } else {
                    dcpu.registers[2] = 0x0000;
                }
                break;
        }
    }

    @Override
    public void tick60hz() {
    }

    // INTERRUPT ROUTINES
    
    private boolean storeRadarSignatures() {
        Array<RadarSignal> signals = RadarManager.getRadarSignals(ship);
        writeSignalsToMemory(dcpu.ram, dcpu.registers[1], signals);
        return true;
    }

    private boolean setTrackingTarget() {
        Entity entity = RadarManager.findBySignature(dcpu.registers[1]);
        
        setTrackingTarget(entity);

        return entity != null;
    }

    private boolean storeTargetData() {
        if(target == null) {
            return false;
        }

        writeTargetDataToMemory(dcpu.ram, dcpu.registers[1], target);
        return true;
    }
     
    private boolean storeOrbitalStateVector() {
        Vector3d ownCoord = Mapper.POSITION.get(ship).getVector3d();
        SOIData soiData = SOIManager.getSOI(ownCoord);

        GravityManager.writeOrbitalStateVectorToMemory(dcpu.ram, dcpu.registers[1], soiData.entity, target, false);
        return true;
    }

    // UTILS
    
    public void writeSignalsToMemory(char[] mem, char ptr, Array<RadarSignal> signals) {
        for(int i = 0; i < 16; i++) {
            if(i >= signals.size) {
                dcpu.ram[ptr++] = 0;
            } else {
                dcpu.ram[ptr++] = (char) signals.get(i).entity.id;
            }
        }
    }

    public void setTrackingTarget(Entity entity) {
        if(target != entity) {
            target = entity;
            dirty = true;
        }
    }

    private void writeTargetDataToMemory(char[] mem, char ptr, Entity target) {
        // target type
        mem[ptr++] = 0x0002;

        // target mass
        int mass = (int) Mapper.MASS.get(target).mass;
        mem[ptr++] = (char) ((mass >> 16) & 0xffff);
        mem[ptr++] = (char) (mass & 0xffff);

        // distance (float)
        Vector3d ownCoord = Mapper.POSITION.get(ship).getVector3d();
        Vector3d othCoord = Mapper.POSITION.get(target).getVector3d();
        
        float distance = (float) ownCoord.distance(othCoord);
        
        ptr = writeFloatToMemory(mem, ptr, distance);
        ptr = writePitchAndYawToMemory(mem, ptr, ship, target);
    }
}
