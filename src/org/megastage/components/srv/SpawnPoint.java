package org.megastage.components.srv;

import org.megastage.ecs.BaseComponent;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.World;

public class SpawnPoint extends BaseComponent {
    public Vector3f vector;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        vector = new Vector3f(
                getFloatValue(element, "x", 0.0f),
                getFloatValue(element, "y", 0.0f),
                getFloatValue(element, "z", 0.0f));
        
        return null;
    }
}
