package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.protocol.Message;
import org.megastage.util.Mapper;

public class VirtualForceField extends DCPUHardware {
    public transient float radius;
    public transient float energy;
    public transient float maxEnergy;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_FORCE_FIELD;
        revision = 0x0944;
        manufactorer = MANUFACTORER_CRADLE_TECH;

        super.init(world, parent, element);
        
        radius = getFloatValue(element, "radius", 20);
        maxEnergy = getFloatValue(element, "max_energy", 40000);
        energy = getFloatValue(element, "energy", 0);
         
        return null;
    }

    @Override
    public void interrupt() {
        switch(dcpu.registers[0]) {
            case 0:
                break;
        }
    }

    @Override
    public Message replicate(Entity entity) {
        dirty = false;
        return ForceFieldData.create(radius, energy).always(entity);
    }
    
    @Override
    public Message synchronize(Entity entity) {
        return replicateIfDirty(entity);
    }

    public void damage(Entity entity, float damage) {
        this.energy -= damage;
        this.dirty = true;

        Log.info("Energy : " + energy);
        
        if(energy <= 0) {
            Mapper.COLLISION_SPHERE.get(entity).radius = 0;
        }
    }
}
