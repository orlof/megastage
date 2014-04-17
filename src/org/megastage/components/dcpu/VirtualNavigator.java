package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

public class VirtualNavigator extends DCPUHardware {
    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_PPS;
        revision = 0x6509;
        manufactorer = MANUFACTORER_TALON_NAVIGATION;

        super.init(world, parent, element);
        
        return null;
    }

    @Override
    public void interrupt() {
        int a = dcpu.registers[0];
        switch(a) {
            case 0:
                break;
            case 1:
                break;
        }
    }

}