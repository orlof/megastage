package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.Vector;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Position extends BaseComponent {
    public long x, y, z;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        x = 1000000 * element.getAttribute("x").getLongValue();
        y = 1000000 * element.getAttribute("y").getLongValue();
        z = 1000000 * element.getAttribute("z").getLongValue();
    }

    public void add(Vector vector) {
        x += Math.round(vector.x);
        y += Math.round(vector.y);
        z += Math.round(vector.z);
    }

}
