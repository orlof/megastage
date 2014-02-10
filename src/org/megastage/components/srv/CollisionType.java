package org.megastage.components.srv;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:58
 */
public class CollisionType extends BaseComponent {
    public static final int SHIP = 0;
    public static final int CELESTIAL = 1;
    
    public int item;
    public double radius;

    public CollisionType(int type, double radius) {
        this.item = type;
        this.radius = radius;
    }

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws DataConversionException {
        radius = getDoubleValue(element, "radius", 0);
        
        String itemName = getStringValue(element, "item", "SHIP");

        switch(itemName) {
            case "SHIP":
                item = SHIP;
                break;
            case "CELESTIAL":
                item = CELESTIAL;
                break;
        }

        return null;
    }

    public boolean isCelestial() {
        return item == CELESTIAL;
    }

    public boolean isShip() {
        return item == SHIP;
    }
}
