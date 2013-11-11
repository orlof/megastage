package org.megastage.components;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

/**
 * MegaStage
 * User: Orlof
 * Date: 17.8.2013
 * Time: 20:11
 */
public abstract class BaseComponent extends Component {
    public abstract void init(World world, Entity parent, Element element) throws Exception;

    protected String getStringValue(Element config, String attrName, String defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        if(attr != null) {
            return attr.getValue();
        }

        return defaultValue;
    }

    protected long getLongValue(Element config, String attrName, long defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        try {
            if(attr != null) {
                return attr.getLongValue();
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    protected int getIntegerValue(Element config, String attrName, int defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        if(attr != null) {
            String value = attr.getValue();

            if(value.startsWith("0x")) {
                return (int) (Long.parseLong(value.substring(2), 16) & 0xffffffff);
            } else {
                return Integer.parseInt(value, 10);
            }
        }

        return defaultValue;
    }

    protected double getDoubleValue(Element config, String attrName, double defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        try {
            if(attr != null) {
                return attr.getDoubleValue();
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    protected float getFloatValue(Element config, String attrName, float defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        try {
            if(attr != null) {
                return attr.getFloatValue();
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
        }

        return defaultValue;
    }
}
