package org.megastage.ecs;

import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.util.Vector3d;

public abstract class BaseComponent extends ToStringComponent {

    // TODO change to static create(...)
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        return null;
    }

    /** This method is called after world is ready **/
    public void initialize(int eid) {}

    /** This method is called when entity is deleted **/
    public void delete(int eid) {}
    
    protected static boolean hasValue(Element config, String arrtName) {
        return config.getAttribute(arrtName) != null;
    }
    
    protected static String getStringValue(Element config, String attrName, String defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        if(attr != null) {
            return attr.getValue();
        }

        return defaultValue;
    }

    protected static boolean getBooleanValue(Element config, String attrName, boolean defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        try {
            if(attr != null) {
                return attr.getBooleanValue();
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    protected static long getLongValue(Element config, String attrName, long defaultValue) {
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

    protected static int getIntegerValue(Element config, String attrName, int defaultValue) {
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

    protected static double getDoubleValue(Element config, String attrName, double defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        if(attr != null) {
            String val = attr.getValue();
            if(val.endsWith("km") || val.endsWith("kg")) {
                val = val.substring(0, val.length()-2);
                return 1000.0f * Double.parseDouble(val);
            }
            return Double.parseDouble(val);
        }

        return defaultValue;
    }

    protected static float getFloatValue(Element config, String attrName, float defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        if(attr != null) {
            String val = attr.getValue();
            if(val.endsWith("km") || val.endsWith("kg")) {
                val = val.substring(0, val.length()-2);
                return 1000.0f * Float.parseFloat(val);
            }
            return Float.parseFloat(val);
        }

        return defaultValue;
    }

    protected static Vector3d getVector3d(Element config, String attrName, Vector3d defaultValue) {
        Attribute attr = config.getAttribute(attrName);

        if(attr != null) {
            String val = attr.getValue();
            String[] vec = val.split(",");
            
            return new Vector3d(
                    Double.parseDouble(vec[0]), 
                    Double.parseDouble(vec[1]), 
                    Double.parseDouble(vec[2]));
        }

        return defaultValue;
    }
}
