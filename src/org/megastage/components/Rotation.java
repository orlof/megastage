package org.megastage.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.megastage.ecs.BaseComponent;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;

public class Rotation extends ReplicatedComponent {
    public Quaternion value = Quaternion.IDENTITY;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        float x = (float) Math.toRadians(getFloatValue(element, "x", 0.0f));
        float y = (float) Math.toRadians(getFloatValue(element, "y", 0.0f));
        float z = (float) Math.toRadians(getFloatValue(element, "z", 0.0f));

        rotate(Vector3f.UNIT_Y, y);
        rotate(Vector3f.UNIT_Z, x);
        rotate(Vector3f.UNIT_X, z);
        
        return null;
    }

    @Override
    public void receive(int eid) {
        if(eid == ClientGlobals.playerEntity) {
            if(World.INSTANCE.hasComponent(eid, CompType.Rotation)) {
                return;
            }
        }

        World.INSTANCE.setComponent(eid, CompType.Rotation, this);
    }
    
    
    public void rotate(Vector3f axis, float radians) {
        if(radians == 0.0) {
            return;
        } 
        
        value.multLocal(axis);
        Quaternion rotation = new Quaternion().fromAngleAxis(radians, axis);
        
        value = rotation.multLocal(value);
    }
}
