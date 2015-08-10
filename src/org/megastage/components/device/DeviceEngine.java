package org.megastage.components.device;

import com.jme3.math.Vector3f;
import org.jdom2.Element;
import org.megastage.components.dcpu.DCPU;
import org.megastage.components.generic.WrapperCharacter;
import org.megastage.ecs.CompType;
import org.megastage.ecs.DirtyComponent;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.util.XmlUtil;

public class DeviceEngine extends DirtyComponent {
    public transient Vector3f vector;
    public transient float maxForce;
    public transient char engineId;
    public char power = 0;

    @Override
    public void config(Element elem) {
        vector = XmlUtil.getVector3fValue(elem, "vector");
        maxForce = XmlUtil.getFloatValue(elem, "max_force");
        engineId = (char) XmlUtil.getIntegerValue(elem, "engine_id");
    }

    public void setPower(char power) {
        if(this.power != power) {
            this.power = power;
            this.dirty = true;
        }
    }
    
    public float getPowerLevel() {
        return power / 65535.0f;
    }

    public Vector3f getForce() {
        return vector.mult(maxForce * getPowerLevel());
    }

    public boolean isActive() {
        return power != 0;
    }
}