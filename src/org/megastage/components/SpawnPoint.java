package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import com.jme3.math.Vector3f;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class SpawnPoint extends BaseComponent {
    public int x, y, z;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        x = getIntegerValue(element, "dx", 0);
        y = getIntegerValue(element, "dy", 0);
        z = getIntegerValue(element, "dz", 0);
        
        return null;
    }

    @Override
    public boolean replicate() {
        return true;
    }
    
    public Vector3f getAsVector() {
        return new Vector3f(x, y, z);
    }
    
    public String toString() {
        return "SpawnPoint(" + x + ", " + y + ", " + z + ")";
    }
}
