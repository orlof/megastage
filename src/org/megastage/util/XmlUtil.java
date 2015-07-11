package org.megastage.util;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.jdom2.Attribute;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.IOException;

public class XmlUtil {
    public static boolean hasValue(Element elem, String attrName) {
        return elem.getAttribute(attrName) != null;
    }

    public static Class getClassValue(Element elem, String attrName) {
        try {
            return Class.forName("org.megastage.components." + getStringValue(elem, attrName));
        } catch (ClassNotFoundException e) {
            throw new MegastageException(e);
        }
    }

    public static Class getClassValue(Element elem, String attrName, Class defaultValue) {
        try {
            return getClassValue(elem, attrName);
        } catch(MegastageException ex) {
            return defaultValue;
        }
    }

    public static String getStringValue(Element elem, String attrName) {
        Attribute attr = elem.getAttribute(attrName);

        if(attr != null) {
            return attr.getValue();
        }

        throw new MegastageException("Unknown attribute %s in %s", attrName, elem.getName());
    }

    public static String getStringValue(Element elem, String attrName, String defaultValue) {
        try {
            return getStringValue(elem, attrName);
        } catch(MegastageException ex) {
            return defaultValue;
        }
    }

    public static boolean getBooleanValue(Element elem, String attrName) {
        Attribute attr = elem.getAttribute(attrName);

        try {
            if(attr != null) {
                return attr.getBooleanValue();
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
        }

        throw new MegastageException("Unknown attribute %s in %s", attrName, elem.getName());
    }

    public static boolean getBooleanValue(Element elem, String attrName, boolean defaultValue) {
        try {
            return getBooleanValue(elem, attrName);
        } catch(MegastageException ex) {
            return defaultValue;
        }
    }

    public static long getLongValue(Element elem, String attrName, long defaultValue) {
        Attribute attr = elem.getAttribute(attrName);

        try {
            if(attr != null) {
                return attr.getLongValue();
            }
        } catch (DataConversionException e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    public static char getCharacterValue(Element elem, String attrName) {
        Attribute attr = elem.getAttribute(attrName);

        if(attr != null) {
            String value = attr.getValue();

            if(value.startsWith("0x")) {
                return (char) (Long.parseLong(value.substring(2), 16) & 0xffff);
            } else {
                return (char) Integer.parseInt(value, 10);
            }
        }

        throw new MegastageException("Unknown attribute %s in %s", attrName, elem.getName());
    }

    public static char getCharacterValue(Element elem, String attrName, char defaultValue) {
        try {
            return getCharacterValue(elem, attrName);
        } catch(MegastageException ex) {
            return defaultValue;
        }
    }

    public static int getIntegerValue(Element elem, String attrName) {
        Attribute attr = elem.getAttribute(attrName);

        if(attr != null) {
            String value = attr.getValue();

            if(value.startsWith("0x")) {
                return (int) (Long.parseLong(value.substring(2), 16) & 0xffffffff);
            } else {
                return Integer.parseInt(value, 10);
            }
        }

        throw new MegastageException("Unknown attribute %s in %s", attrName, elem.getName());
    }

    public static int getIntegerValue(Element elem, String attrName, int defaultValue) {
        try {
            return getIntegerValue(elem, attrName);
        } catch(MegastageException ex) {
            return defaultValue;
        }
    }

    public static float getFloatValue(Element elem, String attrName) {
        Attribute attr = elem.getAttribute(attrName);

        if(attr != null) {
            String val = attr.getValue();
            if(val.endsWith("km") || val.endsWith("kg")) {
                val = val.substring(0, val.length()-2);
                return 1000.0f * Float.parseFloat(val);
            }
            return Float.parseFloat(val);
        }

        throw new MegastageException("Unknown attribute %s in %s", attrName, elem.getName());
    }

    public static float getFloatValue(Element elem, String attrName, float defaultValue) {
        try {
            return getFloatValue(elem, attrName);
        } catch(MegastageException ex) {
            return defaultValue;
        }
    }

    public static double getDoubleValue(Element elem, String attrName) {
        Attribute attr = elem.getAttribute(attrName);

        if(attr != null) {
            String val = attr.getValue();
            if(val.endsWith("km") || val.endsWith("kg")) {
                val = val.substring(0, val.length()-2);
                return 1000.0f * Double.parseDouble(val);
            }
            return Double.parseDouble(val);
        }

        throw new MegastageException("Unknown attribute %s in %s", attrName, elem.getName());
    }

    public static double getDoubleValue(Element elem, String attrName, double defaultValue) {
        try {
            return getDoubleValue(elem, attrName);
        } catch(MegastageException ex) {
            return defaultValue;
        }
    }

    public static Vector3f getVector3fValue(Element elem, String attrName) {
        Vector3f val = getVector3fValue(elem, attrName, null);
        if(val == null) throw new MegastageException("Unknown attribute %s in %s", attrName, elem.getName());
        return val;
    }

    public static Vector3f getVector3fValue(Element elem, String attrName, Vector3f defaultValue) {
        Attribute attr = elem.getAttribute(attrName);

        if(attr != null) {
            String val = attr.getValue();
            String[] vec = val.split(" ");

            return new Vector3f(
                    Float.parseFloat(vec[0]),
                    Float.parseFloat(vec[1]),
                    Float.parseFloat(vec[2]));
        }

        return defaultValue;
    }

    public static Quaternion getQuaternionValue(Element elem, String attrName) {
        Quaternion val = getQuaternionValue(elem, attrName, null);
        if(val == null) throw new MegastageException("Unknown attribute %s in %s", attrName, elem.getName());
        return val;
    }

    public static Quaternion getQuaternionValue(Element elem, String attrName, Quaternion defaultValue) {
        Attribute attr = elem.getAttribute(attrName);

        if(attr != null) {
            String val = attr.getValue();
            String[] vec = val.split(" ");

            float x = (float) Math.toRadians(Float.parseFloat(vec[0]));
            float y = (float) Math.toRadians(Float.parseFloat(vec[1]));
            float z = (float) Math.toRadians(Float.parseFloat(vec[2]));

            return new Quaternion().fromAngles(x, y, z);
        }

        return defaultValue;
    }

    public static ColorRGBA getColorRGBAValue(Element elem, String attrName) {
        ColorRGBA val = getColorRGBAValue(elem, attrName, null);
        if(val == null) throw new MegastageException("Unknown attribute %s in %s", attrName, elem.getName());
        return val;
    }

    public static ColorRGBA getColorRGBAValue(Element elem, String attrName, ColorRGBA defaultValue) {
        Attribute attr = elem.getAttribute(attrName);

        if(attr != null) {
            String val = attr.getValue();
            String[] vec = val.split(" ");

            return new ColorRGBA(
                    Float.parseFloat(vec[0]),
                    Float.parseFloat(vec[1]),
                    Float.parseFloat(vec[2]),
                    Float.parseFloat(vec[3]));
        }

        return defaultValue;
    }

    public static String output(Element root) throws IOException {
        XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
        return serializer.outputString(root);
    }

    public static Element read(String filename) {
        try {
            SAXBuilder builder = new SAXBuilder();

            File xmlFile = new File(filename);

            Document document = builder.build(xmlFile);
            return document.getRootElement();
        } catch(Exception ex) {
            ex.printStackTrace();
            throw new MegastageException(ex);
        }
    }

}
