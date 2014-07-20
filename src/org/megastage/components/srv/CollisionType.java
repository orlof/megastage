package org.megastage.components.srv;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;

public class CollisionType extends BaseComponent {
    public static final int SHIP = 0;
    public static final int CELESTIAL = 1;
    
    public int item;
    public float radius;

    public static CollisionType create(int type, float radius) {
        CollisionType ct = new CollisionType();
        ct.item = type;
        ct.radius = radius;
        return ct;
    }

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        radius = getFloatValue(element, "radius", 0.0f);
        
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
