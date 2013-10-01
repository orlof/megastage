package org.megastage.components;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.Quaternion;
import org.megastage.util.Vector;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class Heading extends BaseComponent {
    Entity parent;
    Quaternion total = new Quaternion();

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        this.parent = parent;
    }

    public Quaternion getGlobalRotation() {
        if(parent == null) return total;
        return total.multiply(parent.getComponent(Heading.class).getGlobalRotation());
    }



}
