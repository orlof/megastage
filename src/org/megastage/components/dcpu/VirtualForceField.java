package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.protocol.Message;

public class VirtualForceField extends DCPUHardware {
    public transient float radius;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_FORCE_FIELD;
        revision = 0x0944;
        manufactorer = MANUFACTORER_CRADLE_TECH;

        super.init(world, parent, element);
        
        radius = getFloatValue(element, "radius", 20);

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
        return ForceFieldData.create(radius).always(entity);
    }
    
    @Override
    public Message synchronize(Entity entity) {
        return never(entity);
    }
}
