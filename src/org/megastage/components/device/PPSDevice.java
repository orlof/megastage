package org.megastage.components.device;

import com.jme3.math.Vector3f;
import org.megastage.components.generic.EntityReference;
import org.megastage.components.generic.WrapperVector3f;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class PPSDevice extends Device {
    public Vector3f getPosition(int eid) {
        EntityReference ref = (EntityReference) World.INSTANCE.getComponent(eid, CompType.BindTo);
        WrapperVector3f vec = (WrapperVector3f) World.INSTANCE.getComponent(ref.eid, CompType.Position);
        return vec.value;
    }
}
