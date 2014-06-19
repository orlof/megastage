package org.megastage.components;

import org.megastage.ecs.BaseComponent;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;
import org.megastage.util.Vector3d;

public class PositionOffset extends ReplicatedComponent {
    public float x, y, z;
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        x = getFloatValue(element, "x", 0.0f);
        y = getFloatValue(element, "y", 0.0f);
        z = getFloatValue(element, "z", 0.0f);
        
        return null;
    }

    public Vector3f getVector3f() {
        return new Vector3f(x, y, z);
    }
    
    public Vector3d getVector3d() {
        return new Vector3d(x, y, z);
    }
    
    public void set(float x, float y, float z) {
        if(this.x != x || this.y != y || this.z != z) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dirty = true;
        }
    }
}
