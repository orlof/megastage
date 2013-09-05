package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class LocalPosition extends BaseComponent {
    public long x, y, z;
    public Entity parent;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        this.parent = parent;

        x = 1000000 * element.getAttribute("x").getLongValue();
        y = 1000000 * element.getAttribute("y").getLongValue();
        z = 1000000 * element.getAttribute("z").getLongValue();
    }
}
