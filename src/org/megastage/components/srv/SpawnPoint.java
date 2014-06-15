package org.megastage.components.srv;

import org.megastage.ecs.BaseComponent;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.World;

public class SpawnPoint extends BaseComponent {
    public int x, y, z;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        x = getIntegerValue(element, "dx", 0);
        y = getIntegerValue(element, "dy", 0);
        z = getIntegerValue(element, "dz", 0);
        
        return null;
    }

    public Vector3f getAsVector() {
        return new Vector3f(x, y, z);
    }
}
